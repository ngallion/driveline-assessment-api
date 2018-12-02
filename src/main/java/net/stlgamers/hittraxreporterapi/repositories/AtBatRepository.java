package net.stlgamers.hittraxreporterapi.repositories;

import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtBatRepository extends JpaRepository<AtBat, Long> {
    List<AtBat> findBySession(Session session);

//    @Query("SELECT COUNT(ab.result) FROM AtBat ab WHERE ab.sessionId = ?1 AND WHERE ab.result = ?2")
//    Integer findNumberOfAtBatsBySessionAndResult(Long sessionId, String result);
}
