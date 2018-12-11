package net.stlgamers.hittraxreporterapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Long id;

    private List<AtBat> atBats;

    public Session(List<AtBat> atBats) {
        this.atBats = atBats;
    }
}
