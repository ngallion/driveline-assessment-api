package net.stlgamers.hittraxreporterapi.util;

import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.AtBatCsvWithHashColumn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AtBatToCsvToEntityConverterWithHashColumn {

    public AtBat convertForHashColumn(AtBatCsvWithHashColumn csv) {
        AtBat atBat = new AtBat();

        csv.replaceEmptyValuesWithNull();

        atBat.setTimestamp(LocalDateTime.parse(csv.getDate().trim(), DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss.SSS")));
        atBat.setSessionTime(csv.getTimeStamp());
        atBat.setPitchVelocity(Double.parseDouble(csv.getPitch()));
        atBat.setStrikeZonePosition(csv.getStrikeZone() == null ? null : Integer.parseInt(csv.getStrikeZone().trim()));
        atBat.setPitchType(csv.getPType());
        atBat.setExitVelocity(Double.parseDouble(csv.getVelo()));
        atBat.setVerticalAngle(csv.getLa() == null ? null : (int) Double.parseDouble(csv.getLa()));
        atBat.setDistance(csv.getDist() == null ? null : (int) Double.parseDouble(csv.getDist()));
        atBat.setResult(csv.getRes());
        atBat.setType(csv.getType());
        atBat.setHorizontalAngle(csv.getHorizAngle() == null ? null : Integer.parseInt(csv.getHorizAngle().trim()));
        atBat.setPts(Integer.parseInt(csv.getPts().trim()));
        atBat.setHandSpeed(Integer.parseInt(csv.getHandSpeed().trim()));
        atBat.setBallVelocity(Integer.parseInt(csv.getBv().trim()));
        atBat.setTriggerToImpact(Integer.parseInt(csv.getTriggerToImpact().trim()));
        atBat.setAa(Integer.parseInt(csv.getAa().trim()));
        atBat.setImpactMomentum(Integer.parseInt(csv.getImpactMomentum().trim()));
        atBat.setStrikeZoneBottom((int) Double.parseDouble(csv.getStrikeZoneBottom().trim()));
        atBat.setStrikeZoneTop((int) Double.parseDouble(csv.getStrikeZoneTop().trim()));
        atBat.setStrikeZoneWidth((int) Double.parseDouble(csv.getStrikeZoneWidth().trim()));
        atBat.setVerticalDistance((int) Double.parseDouble(csv.getVerticalDistance().trim()));
        atBat.setHorizontalDistance((int) Double.parseDouble(csv.getHorizontalDistance().trim()));
        atBat.setPoiX(Double.parseDouble(csv.getPoiX().trim()));
        atBat.setPoiY(Double.parseDouble(csv.getPoiY().trim()));
        atBat.setPoiZ(Double.parseDouble(csv.getPoiZ().trim()));
        atBat.setBatMaterial(csv.getBatMaterial());
        atBat.setUser(csv.getUser());
        atBat.setPitchAngle(csv.getPitchAngle() == null ? null : (int) Double.parseDouble(csv.getPitchAngle().trim()));
        atBat.setBatting(csv.getBatting());
        atBat.setLevel(csv.getLevel());
        atBat.setOpposingPlayer(csv.getOpposingPlayer());
        atBat.setTag(csv.getTag());
        return atBat;
    }
}
