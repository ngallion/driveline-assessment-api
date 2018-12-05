package net.stlgamers.hittraxreporterapi.repositories;

import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtBatRepository extends JpaRepository<AtBat, Long> {
    List<AtBat> findBySession(Session session);

    @Query("SELECT DISTINCT(a.session.id) FROM AtBat a WHERE a.user = ?1 AND a.timestamp > ?2 AND a.timestamp < ?3")
    List<Long> findSessionIdsByPlayerInDateRange(String player, LocalDateTime firstDay, LocalDateTime lastDay);

//    @Query("SELECT COUNT(ab.result) FROM AtBat ab WHERE ab.sessionId = ?1 AND WHERE ab.result = ?2")
//    Integer findNumberOfAtBatsBySessionAndResult(Long sessionId, String result);

    @Query("SELECT DISTINCT(a.user) FROM AtBat a")
    List<String> findAllDistinctPlayerNames();
}
