package net.stlgamers.hittraxreporterapi.util;

import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AtBatCsvToEntityConverter {

    public AtBat convert(AtBatCsv csv) {
        AtBat atBat = new AtBat();

        csv.replaceEmptyValuesWithNull();
        System.out.println(csv);

        atBat.setNumberOfSwing(Integer.parseInt(csv.getAb()));
        atBat.setTimestamp(LocalDateTime.parse(csv.getDate().trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS")));
        atBat.setSessionTime(csv.getTimeStamp());
        atBat.setPitchVelocity((int) Double.parseDouble(csv.getPitch()));
        atBat.setStrikeZonePosition(csv.getStrikeZone() == null ? null : Integer.parseInt(csv.getStrikeZone()));
        atBat.setPitchType(csv.getPType());
        atBat.setExitVelocity((int) Double.parseDouble(csv.getVelo()));
        atBat.setDistance((int) Double.parseDouble(csv.getDist()));
        atBat.setResult(csv.getRes());
        atBat.setType(csv.getType());
        atBat.setHorizontalAngle(Integer.parseInt(csv.getHorizAngle()));
        atBat.setPts(Integer.parseInt(csv.getPts()));
        atBat.setHandSpeed(Integer.parseInt(csv.getHandSpeed()));
        atBat.setBallVelocity(Integer.parseInt(csv.getBv()));
        atBat.setTriggerToImpact(Integer.parseInt(csv.getTriggerToImpact()));
        atBat.setAa(Integer.parseInt(csv.getAa()));
        atBat.setImpactMomentum(Integer.parseInt(csv.getImpactMomentum()));
        atBat.setStrikeZoneBottom((int) Double.parseDouble(csv.getStrikeZoneBottom()));
        atBat.setStrikeZoneTop((int) Double.parseDouble(csv.getStrikeZoneTop()));
        atBat.setStrikeZoneWidth(Integer.parseInt(csv.getStrikeZoneWidth()));
        atBat.setVerticalDistance((int) Double.parseDouble(csv.getVerticalDistance()));
        atBat.setHorizontalDistance((int) Double.parseDouble(csv.getHorizontalDistance()));
        atBat.setPoiX((int) Double.parseDouble(csv.getPoiX()));
        atBat.setPoiY((int) Double.parseDouble(csv.getPoiY()));
        atBat.setPoiZ((int) Double.parseDouble(csv.getPoiZ()));
        atBat.setBatMaterial(csv.getBatMaterial());
        atBat.setUser(csv.getUser());
        atBat.setPitchAngle(csv.getPitchAngle() == null ? null : Integer.parseInt(csv.getPitchAngle()));
        atBat.setBatting(csv.getBatting());
        atBat.setLevel(csv.getLevel());
        atBat.setOpposingPlayer(csv.getOpposingPlayer());
        atBat.setTag(csv.getTag());
        return atBat;
    }
}
