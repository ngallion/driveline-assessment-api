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

    @Query("SELECT DISTINCT(a.user) FROM AtBat a")
    List<String> findAllDistinctPlayerNames();

    @Query("SELECT a FROM AtBat a WHERE a.user like CONCAT('%', ?1) AND a.timestamp > ?2 AND a.timestamp < ?3")
    List<AtBat> findAtBatsByPlayerInDateRange(String player, LocalDateTime firstDay, LocalDateTime lastDay);

    List<AtBat> findAllByUser(String playerName);

    @Query("SELECT a from AtBat a where a.user like CONCAT('%', ?1)")
    List<AtBat> findAllByUserLike(String playerName);
}
