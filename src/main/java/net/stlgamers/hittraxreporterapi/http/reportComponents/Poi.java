package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Poi {
    private PositionRange positionRange;
    private Double avgEv;
    private Double percentBallsInPlay;
}
