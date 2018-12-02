package net.stlgamers.hittraxreporterapi.repositories;

import net.stlgamers.hittraxreporterapi.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

}
