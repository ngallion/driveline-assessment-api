package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ZoneData {
    private Zone position;
    private Integer positionNumber;
    private Double avgExitVelocity;
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
                .mapToDouble(AtBat::getExitVelocity)
                .summaryStatistics()
                .getAverage();
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
        TOP_LEFT(3), TOP_MIDDLE(2), TOP_RIGHT(1),
        MIDDLE_LEFT(6), CENTER(5), MIDDLE_RIGHT(4),
        BOTTOM_LEFT(9), BOTTOM_MIDDLE(8), BOTTOM_RIGHT(7),
        OUTER_TOP_LEFT(11), OUTER_TOP_RIGHT(10), OUTER_BOTTOM_LEFT(13), OUTER_BOTTOM_RIGHT(12),
        OTHER(14);

        public final Integer value;

        Zone(Integer value) {
            this.value = value;
        }
    }
}
