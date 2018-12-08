package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ZoneData {
    private Zone position;
    private Integer positionNumber;
    private Integer avgExitVelocity;
    private Integer avgLaunchAngle;


    public ZoneData(Integer zone, List<AtBat> atBats) {
        List<AtBat> atBatsInZone = atBats
                .stream()
                .filter(atBat -> atBat.getStrikeZonePosition() == zone)
                .collect(Collectors.toList());
        this.position = getZone(zone);
        this.positionNumber = zone;
        this.avgExitVelocity = atBatsInZone
                .stream()
                .map(AtBat::getExitVelocity)
                .collect(Collectors.averagingInt(Integer::intValue))
                .intValue();
        this.avgLaunchAngle = atBatsInZone
                .stream()
                .map(AtBat::getVerticalAngle)
                .collect(Collectors.averagingInt(Integer::intValue))
                .intValue();
    }

    public Zone getZone(Integer value) {
        for(Zone zone : Zone.values()) {
            if (zone.value == value) {
                return zone;
            }
        }
        return null;
    }

    public enum Zone {
        TOP_LEFT(1), TOP_MIDDLE(2), TOP_RIGHT(3),
        MIDDLE_LEFT(4), CENTER(5), MIDDLE_RIGHT(6),
        BOTTOM_LEFT(7), BOTTOM_MIDDLE(8), BOTTOM_RIGHT(9),
        OUTER_TOP_LEFT(10), OUTER_TOP_RIGHT(11), OUTER_BOTTOM_LEFT(12), OUTER_BOTTOM_RIGHT(13),
        OTHER(14);

        public final Integer value;

        Zone(Integer value) {
            this.value = value;
        }
    }
}
