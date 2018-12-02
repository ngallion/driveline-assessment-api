package net.stlgamers.hittraxreporterapi.http;

import lombok.Data;

import java.util.List;

@Data
public class GetReportRequest {
    private List<Long> sessions;
}
