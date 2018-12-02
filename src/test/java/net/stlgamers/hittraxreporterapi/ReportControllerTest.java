package net.stlgamers.hittraxreporterapi;

import net.stlgamers.hittraxreporterapi.conrollers.ReportController;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.services.ReportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Autowired
    private ReportController reportController;

    @Before
    public void setUp() {
        this.reportController = new ReportController(reportService);
    }

//    @Test
//    public void givenCSVFile_whenCSVFormatValid_respondWithData() throws IOException {
//        String testReport = "csv file";
//        AddReportRequest testRequest = new AddReportRequest();
//        testRequest.setReport(testReport);
//
//        Mockito.when(reportService.addReport(testRequest)).thenReturn(new ReportAddedResponse());
//
//        ResponseEntity<ReportAddedResponse> actualResponse = reportController.addReport(testRequest);
//
//        Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.CREATED);
//    }
}
