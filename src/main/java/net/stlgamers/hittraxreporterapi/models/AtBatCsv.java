package net.stlgamers.hittraxreporterapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import net.stlgamers.hittraxreporterapi.util.AtBatCsvToEntityConverter;

import java.lang.reflect.Field;

@Data
@JsonPropertyOrder( {"AB","Date"," Time Stamp"," Pitch"," Strike Zone"," P. Type"," Velo"," LA"," Dist"," Res",
        " Type"," Horiz. Angle"," Pts"," Hand Speed"," BV"," Trigger to Impact"," AA",
        " Impact Momentum"," Strike Zone Bottom"," Strike Zone Top"," Strike Zone Width",
        " Vertical Distance"," Horizontal Distance"," POI X"," POI Y"," POI Z"," Bat Material",
        " User"," Pitch Angle"," Batting"," Level"," Opposing Player"," Tag"})
public class AtBatCsv {
    @JsonProperty("AB")
    private String ab;
    @JsonProperty("Date")
    private String date;
    @JsonProperty(" Time Stamp")
    private String timeStamp;
    @JsonProperty(" Pitch")
    private String pitch;
    @JsonProperty(" Strike Zone")
    private String strikeZone;
    @JsonProperty(" P. Type")
    private String pType;
    @JsonProperty(" Velo")
    private String velo;
    @JsonProperty(" LA")
    private String la;
    @JsonProperty(" Dist")
    private String dist;
    @JsonProperty(" Res")
    private String res;
    @JsonProperty(" Type")
    private String type;
    @JsonProperty(" Horiz. Angle")
    private String horizAngle;
    @JsonProperty(" Pts")
    private String pts;
    @JsonProperty(" Hand Speed")
    private String handSpeed;
    @JsonProperty(" BV")
    private String bv;
    @JsonProperty(" Trigger to Impact")
    private String triggerToImpact;
    @JsonProperty(" AA")
    private String aa;
    @JsonProperty(" Impact Momentum")
    private String impactMomentum;
    @JsonProperty(" Strike Zone Bottom")
    private String strikeZoneBottom;
    @JsonProperty(" Strike Zone Top")
    private String strikeZoneTop;
    @JsonProperty(" Strike Zone Width")
    private String strikeZoneWidth;
    @JsonProperty(" Vertical Distance")
    private String verticalDistance;
    @JsonProperty(" Horizontal Distance")
    private String horizontalDistance;
    @JsonProperty(" POI X")
    private String poiX;
    @JsonProperty(" POI Y")
    private String poiY;
    @JsonProperty(" POI Z")
    private String poiZ;
    @JsonProperty(" Bat Material")
    private String batMaterial;
    @JsonProperty(" User")
    private String user;
    @JsonProperty(" Pitch Angle")
    private String pitchAngle;
    @JsonProperty(" Batting")
    private String batting;
    @JsonProperty(" Level")
    private String level;
    @JsonProperty(" Opposing Player")
    private String opposingPlayer;
    @JsonProperty(" Tag")
    private String tag;

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields())
            if (f.get(this) != null || !(f.get(this) == " "))
                return false;
        return true;
    }

    public void replaceEmptyValuesWithNull() {
        try {
            for (Field f : getClass().getDeclaredFields())
                if (f.get(this).equals(" ") || f.get(this).equals("")){
                    f.set(this, null);
                }
        } catch (IllegalAccessException e) {
            System.out.println(e);
        }

    }
}
