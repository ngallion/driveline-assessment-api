package net.stlgamers.hittraxreporterapi.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.stlgamers.hittraxreporterapi.http.AddReportRequest;
import net.stlgamers.hittraxreporterapi.http.ReportAddedResponse;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    public ReportAddedResponse addReport(AddReportRequest request) throws IOException {



        return new ReportAddedResponse();
    }

    public List<AtBatCsv> createListOfAtBatsFromCsvString(String csvString) throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(AtBatCsv.class).withHeader();
        MappingIterator<AtBatCsv> it = mapper.readerFor(AtBatCsv.class).with(schema)
                .readValues(csvString);
        List<AtBatCsv> all = it.readAll();
        return all;
    }

    public List<AtBat> convertListOfAtBatCSVToAtBatEntities(List<AtBatCsv> csv) throws IllegalAccessException {

        AtBatCsvToEntityConverter converter = new AtBatCsvToEntityConverter();
        List<AtBat> allAtBats = csv.stream().map(converter::convert).collect(Collectors.toList());
        return allAtBats;

    }
}
