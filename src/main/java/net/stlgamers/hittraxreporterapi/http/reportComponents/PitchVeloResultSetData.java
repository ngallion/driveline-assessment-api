package net.stlgamers.hittraxreporterapi.http.reportComponents;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PitchVeloResultSetData {
    private Range range;
    private Double avgEv;
    private Double avgLa;
}
