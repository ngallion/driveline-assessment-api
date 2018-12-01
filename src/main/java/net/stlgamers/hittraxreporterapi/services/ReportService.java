package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    public ReportAddedResponse addReport(AddReportRequest request) throws IOException {



        return new ReportAddedResponse();
    }

    public List<AtBat> createListOfAtBatsFromCsvString(String csvString) throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(AtBat.class).withHeader();
        MappingIterator<AtBat> it = mapper.readerFor(AtBat.class).with(schema)
                .readValues(csvString);
        List<AtBat> all = it.readAll();
        return all;
    }
}
