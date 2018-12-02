package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;
import net.stlgamers.hittraxreporterapi.models.Report;
import net.stlgamers.hittraxreporterapi.models.Session;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.repositories.SessionRepository;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    public ReportAddedResponse addReport(AddReportRequest request) throws IOException {

        List<AtBatCsv> atBatCsvs = createListOfAtBatsFromCsvString(request.getReport());
        List<AtBat> atBats = convertListOfAtBatCSVToAtBatEntities(atBatCsvs);
        Session session = saveSessionToDatabase(atBats);

        return new ReportAddedResponse(session.getId().toString(), "success");
    }

    public Report getReport(List<Long> request) {
        Report report = new Report();

        report.setAvgExitVelocity(sessionService.getAvgExitVelocity(request));
        report.setMaxExitVelocity(sessionService.getMaxExitVelocity(request));
        Long numberOfGroundBalls = sessionService.getNumberResultType(request, "GB");
        Long numberOfFlyBalls = sessionService.getNumberResultType(request, "FB");
        Long numberOfLineDrives = sessionService.getNumberResultType(request, "LD");
        Long total = numberOfFlyBalls + numberOfGroundBalls + numberOfLineDrives;
        Double percentGroundBalls = ((double)numberOfGroundBalls/(double) total) * 100;
        Double percentFlyBalls = ((double)numberOfFlyBalls/(double) total) * 100;
        Double percentLineDrives = ((double)numberOfLineDrives/(double) total) * 100;
        report.setGroundBallPercentage(percentGroundBalls.toString().substring(0, 4));
        report.setFlyBallPercentage(percentFlyBalls.toString().substring(0, 4));
        report.setLineDrivePercentage(percentLineDrives.toString().substring(0, 4));

        return report;
    }


    public List<AtBatCsv> createListOfAtBatsFromCsvString(String csvString) throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(AtBatCsv.class).withHeader();
        MappingIterator<AtBatCsv> it = mapper.readerFor(AtBatCsv.class).with(schema)
                .readValues(csvString);
        List<AtBatCsv> all = it.readAll();
        return all;
    }

    public List<AtBat> convertListOfAtBatCSVToAtBatEntities(List<AtBatCsv> csv) {

        AtBatCsvToEntityConverter converter = new AtBatCsvToEntityConverter();
        List<AtBat> allAtBats = csv
                .stream()
                .filter(element -> (int) Double.parseDouble(element.getVelo()) > 50)
                .map(converter::convert)
                .collect(Collectors.toList());
        return allAtBats;

    }

    public Session saveSessionToDatabase(List<AtBat> atBats) {
        Session session = sessionRepo.save(new Session(atBats));
        atBats.forEach(atBat -> atBat.setSession(session));
        List<AtBat> savedAtBats = atBatRepository.saveAll(atBats);
        
        return session;
    }
}
