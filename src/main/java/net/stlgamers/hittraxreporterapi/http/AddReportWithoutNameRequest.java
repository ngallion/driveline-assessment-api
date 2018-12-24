package net.stlgamers.hittraxreporterapi.http;

import lombok.Data;

@Data
public class AddReportWithoutNameRequest {
    private String report;
    private String name;
}
