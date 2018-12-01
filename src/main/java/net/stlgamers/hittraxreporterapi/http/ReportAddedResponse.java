package net.stlgamers.hittraxreporterapi.http;

import lombok.Data;

@Data
public class ReportAddedResponse {
    private String reportId;
    private String message;
}
