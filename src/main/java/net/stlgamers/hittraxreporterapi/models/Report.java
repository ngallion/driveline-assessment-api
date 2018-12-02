package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;

@Data
public class Report {

    private Integer numberOfSwings;
    private Double avgExitVelocity;
    private Integer maxExitVelocity;
    private Float battingAverage;
    private String groundBallPercentage;
    private String flyBallPercentage;
    private String lineDrivePercentage;

}
