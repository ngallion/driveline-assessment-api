package net.stlgamers.hittraxreporterapi.conrollers;

import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.AddReportWithoutNameRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.Report;
import net.stlgamers.hittraxreporterapi.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@CrossOrigin
@Controller
@RequestMapping("/report")
public class ReportController {

    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/name")
    public ResponseEntity<Report> getReportByPlayerName(@RequestParam("name") List<String> playerNames) {
        Report response = reportService.getReport(playerNames);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<Report> addReport(@RequestBody AddReportRequest request) throws IOException {
        Report response = reportService.addReport(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/without/name")
    public ResponseEntity<Report> addReport(@RequestBody AddReportWithoutNameRequest request) throws IOException {
        Report response = reportService.addReport(request.getReport(), request.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/date")
    public ResponseEntity<Report> getReportByPlayerNameAndDateRange(@RequestParam("name") List<String> playerNames,
                                                                    @RequestParam("start") String start,
                                                                    @RequestParam("end") String end) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDateTime
                .of(LocalDate.parse(start, dateTimeFormatter), LocalDateTime.now().toLocalTime());
        LocalDateTime endDate = LocalDateTime
                .of(LocalDate.parse(end, dateTimeFormatter), LocalDateTime.now().toLocalTime());

        Report report = reportService.getReport(playerNames, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(report);
    }

    @GetMapping("/name/all")
    public ResponseEntity<List<String>> getPlayerNames() {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getAllPlayerNames());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> reportDoesNotExistHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(
                e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<Object> csvParseErrorHandler(DateTimeParseException e) {
        return new ResponseEntity<>("Error reading csv file",
                new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
