package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprayChart {

    private SprayChartDataResult left;
    private SprayChartDataResult center;
    private SprayChartDataResult right;
}
