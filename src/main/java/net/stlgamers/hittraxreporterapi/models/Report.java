package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;
import net.stlgamers.hittraxreporterapi.http.reportComponents.ExitVeloVsLaunchAngleResult;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChart;

import java.util.List;

@Data
public class Report {

    private String playerName;
    private Integer numberOfSwings;
    private Double avgExitVelocity;
    private Integer maxExitVelocity;
    private Double evStdDeviation;
    private Double avgLaunchAngle;
    private Double avgHhbLaunchAngle;
    private Double laStdDeviation;
    private Float battingAverage;
    private String groundBallPercentage;
    private String flyBallPercentage;
    private String lineDrivePercentage;
    private List<ExitVeloVsLaunchAngleResult> exitVeloVsLaunchAngle;
    private SprayChart sprayChart;
    private Double sluggingPercentage;
    private Double contactRate;
    private Double ops;
    private List<ZoneData> strikeZoneData;

}
