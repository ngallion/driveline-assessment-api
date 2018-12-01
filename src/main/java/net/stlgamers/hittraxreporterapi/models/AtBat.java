package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class AtBat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reportId;

    private Integer numberOfSwing;
    private LocalDateTime timestamp;
    private String sessionTime;
    private Float pitchVelocity;
    private Integer strikeZonePosition;
    private String pitchType;
    private Float swingVelocity;
    private Integer verticalAngle;
    private Integer distance;
    private String result;
    private Integer horizontalAngle;
    private Integer pts;
    private Integer handSpeed;
    private Integer ballVelocity;
    private Integer triggerToImpact;
    private Integer aa;
    private Integer impactMomentum;
    private Float strikeZoneBottom;
    private Float strikeZoneTop;
    private Float strikeZoneWidth;
    private Float verticalDistance;
    private Integer horizontalDistance;
    private Integer poiX;
    private Integer poiY;
    private Integer poiZ;
    private String batMaterial;
    private String user;
    private Integer pitchAngle;
    private String batting;
    private String level;
    private String opposingPlayer;
    private String tag;
}
