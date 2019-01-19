package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import http.reportComponents.ContactRate;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;
import net.stlgamers.hittraxreporterapi.models.AtBatCsvWithHashColumn;
import net.stlgamers.hittraxreporterapi.models.Report;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import net.stlgamers.hittraxreporterapi.util.AtBatToCsvToEntityConverterWithHashColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AtBatRepository atBatRepository;

    private StatService statService;

    public ReportService(AtBatRepository atBatRepository, StatService statService) {
        this.atBatRepository = atBatRepository;
        this.statService = statService;
    }

    public Report addReport(AddReportRequest request) throws IOException {

        if (request.getReport().substring(0, 10).contains("#")) {
            List<AtBatCsvWithHashColumn> atBatCsvs = createListOfAtBatsFromCsvWithHashCol(request.getReport());
            List<AtBat> atBats = withHashconvertListOfAtBatFromAtBatCsv(atBatCsvs);
            List<AtBat> savedAtBats = atBatRepository.saveAll(atBats);

            Report report = getReport(savedAtBats);

            return report;
        }

        List<AtBatCsv> atBatCsvs = createListOfAtBatsFromCsvString(request.getReport());
        List<AtBat> atBats = convertListOfAtBatCSVToAtBatEntities(atBatCsvs);
        List<AtBat> savedAtBats = atBatRepository.saveAll(atBats);

        Report report = getReport(savedAtBats);
        return report;
    }

    public Report addReport(String report, String name) throws IOException {
        if (report.substring(0, 10).contains("#")) {
            List<AtBatCsvWithHashColumn> atBatCsvs = createListOfAtBatsFromCsvWithHashCol(report);
            List<AtBat> atBats = withHashconvertListOfAtBatFromAtBatCsv(atBatCsvs);
            return getReportWithNameAdded(name, atBats);
        }

        List<AtBatCsv> atBatCsvs = createListOfAtBatsFromCsvString(report);
        List<AtBat> atBats = convertListOfAtBatCSVToAtBatEntities(atBatCsvs);
        return getReportWithNameAdded(name, atBats);
    }

    private Report getReportWithNameAdded(String name, List<AtBat> atBats) {
        List<AtBat> atBatsWithNameAdded = atBats
                .stream()
                .peek(atBat -> atBat.setUser(name))
                .collect(Collectors.toList());

        List<AtBat> savedAtBats = atBatRepository.saveAll(atBatsWithNameAdded);
        Report generatedReport = getReport(savedAtBats);

        return generatedReport;
    }

    public Report getReport(String user, LocalDateTime startDate, LocalDateTime endDate) {
        List<AtBat> atBats = atBatRepository.findAtBatsByPlayerInDateRange(user, startDate, endDate);

        if (atBats.size() == 0) {
            throw new IllegalArgumentException("No sessions found for player within date range");
        }

        return generateReport(atBats);
    }

    private Report getReport(List<AtBat> atBats) {

        if (atBats.size() == 0) {
            throw new IllegalArgumentException("No sessions found for player within date range");
        }

        return generateReport(atBats);
    }

    public Report generateReport(List<AtBat> atBats) {

        List<AtBat> atBatsAbove50Ev = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() >= 50.0)
                .collect(Collectors.toList());

        Long numberOfGroundBalls = statService.getNumberResultType(atBatsAbove50Ev, "GB");
        Long numberOfFlyBalls = statService.getNumberResultType(atBatsAbove50Ev, "FB");
        Long numberOfLineDrives = statService.getNumberResultType(atBatsAbove50Ev, "LD");
        Long total = numberOfFlyBalls + numberOfGroundBalls + numberOfLineDrives;

        Double percentGroundBalls = ((double)numberOfGroundBalls/(double) total) * 100;
        Double percentFlyBalls = ((double)numberOfFlyBalls/(double) total) * 100;
        Double percentLineDrives = ((double)numberOfLineDrives/(double) total) * 100;

        ContactRate contactRate = statService.calculateContactRate(atBats);
        Double sluggingPercentage = statService.calculateSluggingPercentage(atBats);

        return Report.builder()
                .playerName(atBats.get(0).getUser())
                .avgExitVelocity(statService.getAvgExitVelocity(atBatsAbove50Ev))
                .maxExitVelocity(statService.getMaxExitVelocity(atBatsAbove50Ev))
                .evStdDeviation(statService.getEvStdDeviation(atBatsAbove50Ev))
                .avgLaunchAngle(statService.getAvgVertAngle(atBatsAbove50Ev))
                .avgHhbLaunchAngle(statService.getAvgHhbLaunchAngle(atBatsAbove50Ev))
                .laStdDeviation(statService.getLaStdDeviation(atBatsAbove50Ev))
                .groundBallPercentage(percentGroundBalls)
                .flyBallPercentage(percentFlyBalls)
                .lineDrivePercentage(percentLineDrives)
                .exitVeloVsLaunchAngle(statService.getExitVeloVsLaunchAngleSet(atBatsAbove50Ev))
                .sprayChart(statService.generateSprayChart(atBatsAbove50Ev))
                .strikeZoneData(statService.generateStrikeZoneData(atBats))
                .contactRate(contactRate)
                .sluggingPercentage(sluggingPercentage)
                .ops(statService.calculateOps(sluggingPercentage, contactRate.get()))
                .poiData(statService.generatePoiDataList(atBatsAbove50Ev))
                .pitchVeloData(statService.generatePitchVeloData(atBatsAbove50Ev))
                .build();
    }



    public List<AtBatCsv> createListOfAtBatsFromCsvString(String csvString) throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(AtBatCsv.class).withHeader();
        MappingIterator<AtBatCsv> it = mapper.readerFor(AtBatCsv.class).with(schema)
                .readValues(csvString);
        List<AtBatCsv> all = it.readAll();
        return all;
    }

    public List<AtBatCsvWithHashColumn> createListOfAtBatsFromCsvWithHashCol(String csvString) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(AtBatCsvWithHashColumn.class).withHeader();
        MappingIterator<AtBatCsvWithHashColumn> it = mapper.readerFor(AtBatCsvWithHashColumn.class).with(schema)
                .readValues(csvString);
        List<AtBatCsvWithHashColumn> all = it.readAll();
        return all;
    }

    public List<AtBat> convertListOfAtBatCSVToAtBatEntities(List<AtBatCsv> csv) {
        try{
            AtBatCsvToEntityConverter converter = new AtBatCsvToEntityConverter();
            List<AtBat> allAtBats = csv
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
            return allAtBats;
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    public List<AtBat> withHashconvertListOfAtBatFromAtBatCsv(List<AtBatCsvWithHashColumn> csv) {
        try{
            AtBatToCsvToEntityConverterWithHashColumn converter = new AtBatToCsvToEntityConverterWithHashColumn();
            List<AtBat> allAtBats = csv
                    .stream()
                    .map(converter::convertForHashColumn)
                    .collect(Collectors.toList());
            return allAtBats;
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    public List<String> getAllPlayerNames() {
        return atBatRepository.findAllDistinctPlayerNames();
    }
}
