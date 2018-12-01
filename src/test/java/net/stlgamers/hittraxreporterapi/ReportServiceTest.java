package net.stlgamers.hittraxreporterapi;

import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;
import net.stlgamers.hittraxreporterapi.services.ReportService;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Before
    public void setUp() {
        this.reportService = new ReportService();
    }

    @Test
    public void givenValidCsv_whenAddReport_thenReportResponseReturned() throws IOException {
        String report =
                "\" AB\",\"Date\",\" Time Stamp\",\" Pitch\",\" Strike Zone\",\" P. Type\",\" Velo\",\" LA\",\" Dist\",\" Res\",\" Type\",\" Horiz. Angle\",\" Pts\",\" Hand Speed\",\" BV\",\" Trigger to Impact\",\" AA\",\" Impact Momentum\",\" Strike Zone Bottom\",\" Strike Zone Top\",\" Strike Zone Width\",\" Vertical Distance\",\" Horizontal Distance\",\" POI X\",\" POI Y\",\" POI Z\",\" Bat Material\",\" User\",\" Pitch Angle\",\" Batting\",\" Level\",\" Opposing Player\",\" Tag\"\n" +
                        "\"1\",\" 11/26/2018 16:08:24.883\",\" 0:0:0.000\",\"0\",\" \",\" N/A\",\"52.9\",\"21\",\"116\",\" 4-3\",\" GB\",\"4\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"21.9\",\"41.2\",\"17\",\"-31.6\",\"0\",\"0\",\"0\",\"0\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"";


        AddReportRequest request = new AddReportRequest();
        request.setReport(report);

        ReportAddedResponse actualResponse = reportService.addReport(request);

        Assert.assertNotNull(actualResponse);
    }

    @Test
    public void givenConvert_whenFileIsFormatted_thenEntityObjectReturned() throws IOException, IllegalAccessException{
        AtBatCsvToEntityConverter converter = new AtBatCsvToEntityConverter();
        String csv =
                "\" AB\",\"Date\",\" Time Stamp\",\" Pitch\",\" Strike Zone\",\" P. Type\",\" Velo\",\" LA\",\" Dist\",\" Res\",\" Type\",\" Horiz. Angle\",\" Pts\",\" Hand Speed\",\" BV\",\" Trigger to Impact\",\" AA\",\" Impact Momentum\",\" Strike Zone Bottom\",\" Strike Zone Top\",\" Strike Zone Width\",\" Vertical Distance\",\" Horizontal Distance\",\" POI X\",\" POI Y\",\" POI Z\",\" Bat Material\",\" User\",\" Pitch Angle\",\" Batting\",\" Level\",\" Opposing Player\",\" Tag\"\n" +
                        "\"1\",\" 11/26/2018 16:08:24.883\",\" 0:0:0.000\",\"0\",\" \",\" N/A\",\"52.9\",\"21\",\"116\",\" 4-3\",\" GB\",\"4\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"21.9\",\"41.2\",\"17\",\"-31.6\",\"0\",\"0\",\"0\",\"0\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"";

        List<AtBatCsv> csvs = reportService.createListOfAtBatsFromCsvString(csv);

        System.out.println(csvs);

        List<AtBat> actualAtBats = reportService.convertListOfAtBatCSVToAtBatEntities(csvs);

        System.out.println(actualAtBats);
    }
}
