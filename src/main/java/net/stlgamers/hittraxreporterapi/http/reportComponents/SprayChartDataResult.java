package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprayChartDataResult {
    private String direction;
    private String percent;
    private String avgExitVelo;
    private String avgVertAngle;
}
