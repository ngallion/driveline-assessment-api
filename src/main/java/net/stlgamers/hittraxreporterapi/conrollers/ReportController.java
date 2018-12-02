package net.stlgamers.hittraxreporterapi.conrollers;

import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.Report;
import net.stlgamers.hittraxreporterapi.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportAddedResponse> addReport(@RequestBody AddReportRequest request) throws IOException {
        ReportAddedResponse response = reportService.addReport(request);

        return ResponseEntity.created(URI.create("report/" + response.getReportId())).body(response);
    }

    @GetMapping
    public ResponseEntity<Report> getReport(@RequestParam("sessionIds") List<Long> sessionIds) {
        Report report = reportService.getReport(sessionIds);
        return ResponseEntity.status(HttpStatus.OK).body(report);
    }

}
