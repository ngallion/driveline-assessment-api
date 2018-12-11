package net.stlgamers.hittraxreporterapi.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.stlgamers.hittraxreporterapi.http.reportComponents.ExitVeloVsLaunchAngleResult;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChartDataResult;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.util.Averager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private AtBatRepository atBatRepository;

    public SessionService(AtBatRepository atBatRepository) {
        this.atBatRepository = atBatRepository;
    }

    public Double getAvgExitVelocity(List<AtBat> atBats) {

        Double avgExitVelo = atBats
                .stream()
                .map(AtBat::getExitVelocity)
                .reduce(new Averager(), Averager::accept, Averager::combine)
                .average();

        return avgExitVelo;
    }

    public Integer getMaxExitVelocity(List<AtBat> atBats) {

        List<Integer> allExitVelocity = atBats
                .stream()
                .map(AtBat::getExitVelocity)
                .sorted()
                .collect(Collectors.toList());

        return allExitVelocity.get(allExitVelocity.size() - 1);
    }

    public Integer getNumberOfHits(List<AtBat> atBats) {
        List<String> allHits = atBats
                .stream()
                .map(AtBat::getResult)
                .filter(result -> result.toLowerCase().contains("b"))
                .collect(Collectors.toList());

        return allHits.size();

    }

    public Long getNumberResultType(List<AtBat> atBats, String resultType) {
        return atBats
                .stream()
                .map(AtBat::getType)
                .filter(result -> result.trim().equals(resultType))
                .count();
    }

    public SprayChartDataResult getSprayChartDataResult(List<AtBat> atBats, AngleRange angleRange) {
        List<AtBat> atBatsInRange = atBats
                .stream()
                .filter(atBat ->
                        atBat.getHorizontalAngle() > angleRange.getLowerLimit()
                                && atBat.getHorizontalAngle() < angleRange.getUpperLimit())
                .collect(Collectors.toList());

        Double avgExitVelocity = getAvgExitVelocity(atBatsInRange);
        Double avgVertAngle = getAvgVertAngle(atBatsInRange);
        Double percentInRange = (double) atBatsInRange.size() / atBats.size() * 100;

        String direction =
                angleRange.getUpperLimit() == -15 ? "Left" :
                        angleRange.getUpperLimit() == 15 ? "Middle"
                                : "Right";

        return new SprayChartDataResult(direction, percentInRange.toString(),
                avgExitVelocity.toString(), avgVertAngle.toString());
    }

    public Double getAvgVertAngle(List<AtBat> atBats) {
        return atBats
                .stream()
                .map(AtBat::getVerticalAngle)
                .reduce(new Averager(), Averager::accept, Averager::combine)
                .average();
    }

    public ExitVeloVsLaunchAngleResult getResultOfExitVeloVsLaunchAngle(List<AtBat> atBats, Integer lowerLimit, Integer upperLimit) {

        List<AtBat> atBatsInRange = atBats
                .stream()
                .filter(result -> result.getVerticalAngle() != null && result.getVerticalAngle() > lowerLimit && result.getVerticalAngle() < upperLimit)
                .collect(Collectors.toList());

        String range = upperLimit ==
                -10 ? "< -10" : lowerLimit == 40 ? "> 40" : lowerLimit.toString() + " - " + upperLimit.toString();

        if (atBatsInRange.size() == 0) {
            return new ExitVeloVsLaunchAngleResult(range, "N/A", "N/A", "0");
        }

        List<Integer> allSortedExitVelocity = atBatsInRange.stream()
                .map(AtBat::getExitVelocity)
                .sorted()
                .collect(Collectors.toList());

        Integer maxExitVelocity = allSortedExitVelocity.get(allSortedExitVelocity.size() - 1);
        Double avgExitVelocity = allSortedExitVelocity
                .stream()
                .reduce(new Averager(), Averager::accept, Averager::combine)
                .average();
        Double percentOfResults = (((double) allSortedExitVelocity.size() / atBats.size()) * 100);

        return new ExitVeloVsLaunchAngleResult(range, maxExitVelocity.toString(),
                avgExitVelocity.toString(), percentOfResults.toString());
    }

    public Double getEvStdDeviation(List<AtBat> atBats) {
        Double average = atBats
                .stream()
                .mapToInt(AtBat::getExitVelocity)
                .summaryStatistics()
                .getAverage();

        Double avgOfDifferenceOfMeanAndElements= atBats
                .stream()
                .mapToDouble(atBat -> Math.pow((atBat.getExitVelocity() - average), 2 ))
                .summaryStatistics()
                .getAverage();

        return Math.sqrt(avgOfDifferenceOfMeanAndElements);

    }

    @Data
    @AllArgsConstructor
    public static class AngleRange {
        private Integer lowerLimit;
        private Integer upperLimit;
    }




}
