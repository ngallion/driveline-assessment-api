package net.stlgamers.hittraxreporterapi.services;

import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.Session;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AtBatRepository atBatRepository;

    public SessionService(SessionRepository sessionRepository, AtBatRepository atBatRepository) {
        this.sessionRepository = sessionRepository;
        this.atBatRepository = atBatRepository;
    }

    public Double getAvgExitVelocity(List<Long> sessionIds) {

        List<AtBat> atBats = getAllAtBatsAbove50EVForAllSessionsById(sessionIds);

        Double avgExitVelo = atBats
                .stream()
                .map(atBat -> atBat.getExitVelocity())
                .reduce(new Averager(), Averager::accept, Averager::combine)
                .average();

        return avgExitVelo;
    }

    public Integer getMaxExitVelocity(List<Long> sessionIds) {

        List<AtBat> atBats = getAllAtBatsAbove50EVForAllSessionsById(sessionIds);

        List<Integer> allExitVelocity = atBats
                .stream()
                .map(AtBat::getExitVelocity)
                .sorted()
                .collect(Collectors.toList());

        return allExitVelocity.get(allExitVelocity.size() - 1);
    }

    public Integer getNumberOfHits(List<Long> sessionIds) {
        List<AtBat> atBats = getAllAtBatsAbove50EVForAllSessionsById(sessionIds);

        List<String> allHits = atBats
                .stream()
                .map(AtBat::getResult)
                .filter(result -> result.toLowerCase().contains("b"))
                .collect(Collectors.toList());

        return allHits.size();

    }

    public Long getNumberResultType(List<Long> sessionIds, String resultType) {
        List<AtBat> atBats = getAllAtBatsAbove50EVForAllSessionsById(sessionIds);

        return atBats
                .stream()
                .map(AtBat::getType)
                .filter(result -> result.trim().equals(resultType))
                .count();
    }

    public List<AtBat> getAllAtBatsForAllSessionsById(List<Long> sessionIds) {
        List<Session> sessions = new ArrayList<>();
        sessionIds.forEach(id -> sessions.add(sessionRepository.findById(id).get()));

        List<AtBat> atBats = new ArrayList<>();
        sessions.forEach(session -> atBats.addAll(atBatRepository.findBySession(session)));

        return atBats;
    }

    public List<AtBat> getAllAtBatsAbove50EVForAllSessionsById(List<Long> sessionIds) {

        List<AtBat> atBats = getAllAtBatsForAllSessionsById(sessionIds);

        List<AtBat> filteredAtBats = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50)
                .collect(Collectors.toList());

        return filteredAtBats;
    }

    static class Averager {
        private final Integer total;
        private final Integer count;

        public Averager() {
            this.total = 0;
            this.count = 0;
        }

        public Averager(Integer total, Integer count) {
            this.total = total;
            this.count = count;
        }

        public double average() {
            return count > 0 ? ((double) total) / count : 0;
        }

        public Averager accept(Integer i) {
            return new Averager(total + i, count + 1);
        }

        public Averager combine(Averager other) {
            return new Averager(total + other.total, count + other.count);
        }
    }


}
