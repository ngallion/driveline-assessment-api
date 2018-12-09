package net.stlgamers.hittraxreporterapi.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AtBat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SESSION_ID")
    private Session session;

    private LocalDateTime timestamp;
    private String sessionTime;
    private Integer pitchVelocity;
    private Integer strikeZonePosition;
    private String pitchType;
    private Integer exitVelocity;
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
