package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionRange {
    private Double upperBound;
    private Double lowerBound;
}
