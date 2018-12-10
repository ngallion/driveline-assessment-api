package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.reportComponents.ExitVeloVsLaunchAngleResult;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChart;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChartDataResult;
import net.stlgamers.hittraxreporterapi.models.*;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.repositories.SessionRepository;
import net.stlgamers.hittraxreporterapi.services.SessionService.AngleRange;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import net.stlgamers.hittraxreporterapi.util.AtBatToCsvToEntityConverterWithHashColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReportService {

    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private AtBatRepository atBatRepository;

    private SessionService sessionService;

    public ReportService(SessionRepository sessionRepo, AtBatRepository atBatRepository, SessionService sessionService) {
        this.sessionRepo = sessionRepo;
        this.atBatRepository = atBatRepository;
        this.sessionService = sessionService;
    }

    public Report addReport(AddReportRequest request) throws IOException {

        if (request.getReport().substring(0, 10).contains("#")) {
            List<AtBatCsvWithHashColumn> atBatCsvs = createListOfAtBatsFromCsvWithHashCol(request.getReport());
            List<AtBat> atBats = withHashconvertListOfAtBatFromAtBatCsv(atBatCsvs);
            Session session = saveSessionToDatabase(atBats);

            Report report = getReport(Arrays.asList(session.getId()));

            return report;
        }

        List<AtBatCsv> atBatCsvs = createListOfAtBatsFromCsvString(request.getReport());
        List<AtBat> atBats = convertListOfAtBatCSVToAtBatEntities(atBatCsvs);
        Session session = saveSessionToDatabase(atBats);

        Report report = getReport(Arrays.asList(session.getId()));

        return report;
    }

    public Report getReport(List<Long> request) {
        List<AtBat> atBats = sessionService.getAllAtBatsForAllSessionsById(request);
        if (atBats.size() == 0) {
            throw new IllegalArgumentException("No sessions found for player within date range");
        }

        Report report = generateReport(atBats);

        return report;
    }

    public Report generateReport(List<AtBat> atBats) {
        Report report = new Report();

        List<AtBat> atBatsAbove50Ev = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50)
                .collect(Collectors.toList());

        report.setPlayerName(atBats.get(0).getUser());
        report.setAvgExitVelocity(sessionService.getAvgExitVelocity(atBatsAbove50Ev));
        report.setMaxExitVelocity(sessionService.getMaxExitVelocity(atBatsAbove50Ev));
        report.setEvStdDeviation(sessionService.getEvStdDeviation(atBatsAbove50Ev));

        Long numberOfGroundBalls = sessionService.getNumberResultType(atBatsAbove50Ev, "GB");
        Long numberOfFlyBalls = sessionService.getNumberResultType(atBatsAbove50Ev, "FB");
        Long numberOfLineDrives = sessionService.getNumberResultType(atBatsAbove50Ev, "LD");
        Long total = numberOfFlyBalls + numberOfGroundBalls + numberOfLineDrives;

        Double percentGroundBalls = ((double)numberOfGroundBalls/(double) total) * 100;
        Double percentFlyBalls = ((double)numberOfFlyBalls/(double) total) * 100;
        Double percentLineDrives = ((double)numberOfLineDrives/(double) total) * 100;

        report.setGroundBallPercentage(percentGroundBalls.toString().substring(0, 4));
        report.setFlyBallPercentage(percentFlyBalls.toString().substring(0, 4));
        report.setLineDrivePercentage(percentLineDrives.toString().substring(0, 4));
        report.setExitVeloVsLaunchAngle(getExitVeloVsLaunchAngleSet(atBatsAbove50Ev));

        report.setSprayChart(generateSprayChart(atBatsAbove50Ev));
        report.setStrikeZoneData(generateStrikeZoneData(atBats));

        Double contactRate = calculateContactRate(atBats);
        Double sluggingPercentage = calculateSluggingPercentage(atBats);

        report.setContactRate(contactRate);
        report.setSluggingPercentage(sluggingPercentage);
        report.setOps(calculateOps(sluggingPercentage, contactRate));

        return report;
    }

    private Double calculateOps(Double slugging, Double contactRate) {
        return (1.374 * slugging) + (0.411 * (contactRate/100));
    }

    private Integer getPointValue(AtBat atBat) {
        if (atBat.getResult() == null) {
            return 0;
        }
        if (atBat.getResult().length() < 3 && !atBat.getResult().toLowerCase().equals("hr")) {
            return 0;
        }
        String result = atBat.getResult().trim().toLowerCase().substring(0, 2);
        Integer value;
        switch (result) {
            case "1b": value = 1;
            break;
            case "2b": value = 2;
            break;
            case "3b": value = 3;
            break;
            case "hr": value = 4;
            break;
            default: value = 0;
            break;
        }
        return value;
    }

    private Double calculateSluggingPercentage(List<AtBat> atBats) {

        Integer totalPoints = atBats
                .stream()
                .map(this::getPointValue)
                .reduce(0,(a, b) -> a + b);

        Integer totalBallsInPlay = Math.toIntExact(atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50 && atBat.getPitchVelocity() > 50)
                .count());

        return ((double)totalPoints / (double)totalBallsInPlay);
    }

    private Double calculateContactRate(List<AtBat> atBats) {
        Integer numberOfAtBatsWith50PlusEv = Math.toIntExact(atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50)
                .count());

        Integer numberOfPitchesInStrikeZone = Math.toIntExact(atBats
                .stream()
                .filter(atBat ->
                        atBat.getStrikeZonePosition() != null
                                && atBat.getStrikeZonePosition() > 0
                                && atBat.getStrikeZonePosition() < 10)
                .count());

        return  ((double)numberOfAtBatsWith50PlusEv / (double)numberOfPitchesInStrikeZone) * 100;
    }

    public List<ZoneData> generateStrikeZoneData(List<AtBat> atBats) {
        List<AtBat> filteredAtBats = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 0 && atBat.getVerticalAngle() != null)
                .collect(Collectors.toList());

        return IntStream.range(1, 15)
                .mapToObj(zone -> new ZoneData(zone, filteredAtBats))
                .collect(Collectors.toList());
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
                    .filter(element -> element.getVelo() != null && !element.getVelo().isEmpty())
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
                    .filter(element -> element.getVelo() != null && !element.getVelo().isEmpty())
                    .map(converter::convertForHashColumn)
                    .collect(Collectors.toList());
            return allAtBats;
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    public List<Long> getSessionIdsInDateRange(String player, LocalDateTime start, LocalDateTime end) {
        return sessionService.getAtBatsInDateRange(player, start, end);
    }

    public List<ExitVeloVsLaunchAngleResult> getExitVeloVsLaunchAngleSet(List<AtBat> atBats) {
        List<ExitVeloVsLaunchAngleResult> set = Arrays.asList(
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, -50, -10),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, -10, 0),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, 0, 10),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, 10, 20),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, 20, 30),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, 30, 40),
                sessionService.getResultOfExitVeloVsLaunchAngle(atBats, 40, 100)
        );

        return set;
    }

    public SprayChart generateSprayChart(List<AtBat> atBats) {

        AngleRange leftAngle = new AngleRange(-45, -15);
        AngleRange centerAngle = new AngleRange(-15, 15);
        AngleRange rightAngle = new AngleRange(15, 45);

        SprayChartDataResult left = sessionService.getSprayChartDataResult(atBats, leftAngle);
        SprayChartDataResult center = sessionService.getSprayChartDataResult(atBats, centerAngle);
        SprayChartDataResult right = sessionService.getSprayChartDataResult(atBats, rightAngle);

        return new SprayChart(left, center, right);
    }

    public Session saveSessionToDatabase(List<AtBat> atBats) {
        Session session = sessionRepo.save(new Session(atBats));
        atBats.forEach(atBat -> atBat.setSession(session));
        List<AtBat> savedAtBats = atBatRepository.saveAll(atBats);
        
        return session;
    }

    public List<String> getAllPlayerNames() {
        return atBatRepository.findAllDistinctPlayerNames();
    }
}
