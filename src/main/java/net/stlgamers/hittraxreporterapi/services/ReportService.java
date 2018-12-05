package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.reportComponents.ExitVeloVsLaunchAngleResult;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChart;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChartDataResult;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;
import net.stlgamers.hittraxreporterapi.models.Report;
import net.stlgamers.hittraxreporterapi.models.Session;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.repositories.SessionRepository;
import net.stlgamers.hittraxreporterapi.services.SessionService.AngleRange;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    public Report addReport(AddReportRequest request) throws IOException {

        List<AtBatCsv> atBatCsvs = createListOfAtBatsFromCsvString(request.getReport());
        List<AtBat> atBats = convertListOfAtBatCSVToAtBatEntities(atBatCsvs);
        Session session = saveSessionToDatabase(atBats);

        Report report = getReport(Arrays.asList(session.getId()));

        return report;
    }

    public Report getReport(List<Long> request) {
        List<AtBat> atBats = sessionService.getAllAtBatsAbove50EVForAllSessionsById(request);

        Report report = generateReport(atBats);

        return report;
    }

    public Report generateReport(List<AtBat> atBats) {
        Report report = new Report();

        report.setAvgExitVelocity(sessionService.getAvgExitVelocity(atBats));
        report.setMaxExitVelocity(sessionService.getMaxExitVelocity(atBats));

        Long numberOfGroundBalls = sessionService.getNumberResultType(atBats, "GB");
        Long numberOfFlyBalls = sessionService.getNumberResultType(atBats, "FB");
        Long numberOfLineDrives = sessionService.getNumberResultType(atBats, "LD");
        Long total = numberOfFlyBalls + numberOfGroundBalls + numberOfLineDrives;

        Double percentGroundBalls = ((double)numberOfGroundBalls/(double) total) * 100;
        Double percentFlyBalls = ((double)numberOfFlyBalls/(double) total) * 100;
        Double percentLineDrives = ((double)numberOfLineDrives/(double) total) * 100;

        report.setGroundBallPercentage(percentGroundBalls.toString().substring(0, 4));
        report.setFlyBallPercentage(percentFlyBalls.toString().substring(0, 4));
        report.setLineDrivePercentage(percentLineDrives.toString().substring(0, 4));
        report.setExitVeloVsLaunchAngle(getExitVeloVsLaunchAngleSet(atBats));

        report.setSprayChart(generateSprayChart(atBats));

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
                .filter(element -> element.getVelo() != null && !element.getVelo().isEmpty() && (int) Double.parseDouble(element.getVelo()) > 50)
                .map(converter::convert)
                .collect(Collectors.toList());
        return allAtBats;

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
