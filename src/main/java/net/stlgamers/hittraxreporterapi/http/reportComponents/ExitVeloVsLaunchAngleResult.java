package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExitVeloVsLaunchAngleResult {
    private String range;
    private String maxEv;
    private String avgEv;
    private String percentOfResults;
}
