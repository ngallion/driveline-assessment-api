package net.stlgamers.hittraxreporterapi.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAddedResponse {
    private String reportId;
    private String message;
}
