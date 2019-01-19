package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExitVeloVsLaunchAngleResult {
    private String range;
    private String maxEv;
    private String avgEv;
    private String percentOfResults;
}
