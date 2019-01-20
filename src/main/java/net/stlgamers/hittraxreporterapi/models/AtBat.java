package net.stlgamers.hittraxreporterapi.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AtBat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String sessionTime;
    private Double pitchVelocity;

    private Integer strikeZonePosition;
    private String pitchType;
    private Double exitVelocity;
    private Integer verticalAngle;
    private Integer distance;
    private String result;
    private String type;
    private Integer horizontalAngle;
    private Integer pts;
    private Integer handSpeed;
    private Integer ballVelocity;
    private Integer triggerToImpact;
    private Integer aa;
    private Integer impactMomentum;
    private Integer strikeZoneBottom;
    private Integer strikeZoneTop;
    private Integer strikeZoneWidth;
    private Integer verticalDistance;
    private Integer horizontalDistance;
    private Double poiX;
    private Double poiY;
    private Double poiZ;
    private String batMaterial;
    @Column(name = "player_name")
    private String user;
    private Integer pitchAngle;
    private String batting;
    private String level;
    private String opposingPlayer;
    private String tag;
}
