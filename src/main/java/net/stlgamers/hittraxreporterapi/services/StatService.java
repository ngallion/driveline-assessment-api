package net.stlgamers.hittraxreporterapi.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.stlgamers.hittraxreporterapi.http.reportComponents.ExitVeloVsLaunchAngleResult;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChart;
import net.stlgamers.hittraxreporterapi.http.reportComponents.SprayChartDataResult;
import net.stlgamers.hittraxreporterapi.models.AtBat;
import net.stlgamers.hittraxreporterapi.models.ZoneData;
import net.stlgamers.hittraxreporterapi.repositories.AtBatRepository;
import net.stlgamers.hittraxreporterapi.util.Averager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StatService {

    @Autowired
    private AtBatRepository atBatRepository;

    public StatService(AtBatRepository atBatRepository) {
        this.atBatRepository = atBatRepository;
    }

    public Double getAvgExitVelocity(List<AtBat> atBats) {

        Double avgExitVelo = atBats
                .stream()
                .mapToDouble(AtBat::getExitVelocity)
                .summaryStatistics()
                .getAverage();

        return avgExitVelo;
    }

    public Double getMaxExitVelocity(List<AtBat> atBats) {

        List<Double> allExitVelocity = atBats
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
                        atBat.getHorizontalAngle() >= angleRange.getLowerLimit()
                                && atBat.getHorizontalAngle() <= angleRange.getUpperLimit())
                .collect(Collectors.toList());

        Double avgExitVelocity = getAvgExitVelocity(atBatsInRange);
        Double avgVertAngle = getAvgVertAngle(atBatsInRange);
        Double percentInRange = ((double) atBatsInRange.size() / atBats.size()) * 100;

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
                .mapToInt(AtBat::getVerticalAngle)
                .summaryStatistics()
                .getAverage();
    }

    public ExitVeloVsLaunchAngleResult getResultOfExitVeloVsLaunchAngle(List<AtBat> atBats, Integer lowerLimit, Integer upperLimit) {

        List<AtBat> ballsInPlay = atBats
                .stream()
                .filter(result -> result.getHorizontalAngle() > -45 && result.getHorizontalAngle() < 45)
                .collect(Collectors.toList());

        List<AtBat> atBatsInRange = atBats
                .stream()
                .filter(result ->
                        result.getHorizontalAngle() > -45 && result.getHorizontalAngle() < 45 &&
                        result.getVerticalAngle() >= lowerLimit && result.getVerticalAngle() < upperLimit)
                .collect(Collectors.toList());

        String range = upperLimit ==
                -10 ? "< -10" : lowerLimit == 40 ? "> 40" : lowerLimit.toString() + " - " + upperLimit.toString();

        if (atBatsInRange.size() == 0) {
            return new ExitVeloVsLaunchAngleResult(range, "N/A", "N/A", "0");
        }

        List<Double> allSortedExitVelocity = atBatsInRange.stream()
                .map(AtBat::getExitVelocity)
                .sorted()
                .collect(Collectors.toList());

        Double maxExitVelocity = allSortedExitVelocity
                .stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .getAsDouble();
        Double avgExitVelocity = allSortedExitVelocity
                .stream()
                .mapToDouble(Double::doubleValue)
                .summaryStatistics()
                .getAverage();

        Double percentOfResults = (((double) allSortedExitVelocity.size() / ballsInPlay.size()) * 100);

        return new ExitVeloVsLaunchAngleResult(range, maxExitVelocity.toString(),
                avgExitVelocity.toString(), percentOfResults.toString());
    }

    public Double getEvStdDeviation(List<AtBat> atBats) {
        Double average = atBats
                .stream()
                .mapToDouble(AtBat::getExitVelocity)
                .summaryStatistics()
                .getAverage();

        Double avgOfDifferenceOfMeanAndElements= atBats
                .stream()
                .mapToDouble(atBat -> Math.pow((atBat.getExitVelocity() - average), 2 ))
                .summaryStatistics()
                .getAverage();

        return Math.sqrt(avgOfDifferenceOfMeanAndElements);

    }

    public Double calculateOps(Double slugging, Double contactRate) {
        return (1.374 * slugging) + (0.411 * (contactRate/100));
    }

    private Integer getPointValue(AtBat atBat) {
        if (atBat.getResult() == null) {
            return 0;
        }
        if (atBat.getResult().length() < 3 && !atBat.getResult().toLowerCase().equals("hr")) {
            return 0;
        }
        String result = atBat.getResult().trim().toLowerCase().substring(0, 2);
        Integer value;
        switch (result) {
            case "1b": value = 1;
                break;
            case "2b": value = 2;
                break;
            case "3b": value = 3;
                break;
            case "hr": value = 4;
                break;
            default: value = 0;
                break;
        }
        return value;
    }

    public Double calculateSluggingPercentage(List<AtBat> atBats) {

        Integer totalPoints = atBats
                .stream()
                .map(this::getPointValue)
                .reduce(0,(a, b) -> a + b);

        Integer totalBallsInPlay = Math.toIntExact(atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50 && atBat.getPitchVelocity() > 50)
                .count());

        return ((double)totalPoints / (double)totalBallsInPlay);
    }

    public Double calculateContactRate(List<AtBat> atBats) {
        Integer numberOfAtBatsWith50PlusEvAndResult = Math.toIntExact(atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50.0 && atBat.getHorizontalAngle() > -45.0 && atBat.getHorizontalAngle() < 45.0)
                .count());

        Integer numberOfPitchesInStrikeZone = Math.toIntExact(atBats
                .stream()
                .filter(atBat ->
                        (atBat.getStrikeZonePosition() != null
                                && atBat.getStrikeZonePosition() > 0
                                && atBat.getStrikeZonePosition() < 10) ||
                                (atBat.getExitVelocity() > 50.0 && atBat.getHorizontalAngle() > -45.0 && atBat.getHorizontalAngle() < 45.0))
                .count());

        return  ((double)numberOfAtBatsWith50PlusEvAndResult / numberOfPitchesInStrikeZone) * 100;
    }

    public List<ZoneData> generateStrikeZoneData(List<AtBat> atBats) {
        List<AtBat> filteredAtBats = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 50.0 && atBat.getVerticalAngle() != null)
                .collect(Collectors.toList());

        return IntStream.range(1, 15)
                .mapToObj(zone -> new ZoneData(zone, filteredAtBats))
                .collect(Collectors.toList());
    }

    public List<ExitVeloVsLaunchAngleResult> getExitVeloVsLaunchAngleSet(List<AtBat> atBats) {
        List<ExitVeloVsLaunchAngleResult> set = Arrays.asList(
                getResultOfExitVeloVsLaunchAngle(atBats, -100, -10),
                getResultOfExitVeloVsLaunchAngle(atBats, -10, 0),
                getResultOfExitVeloVsLaunchAngle(atBats, 0, 10),
                getResultOfExitVeloVsLaunchAngle(atBats, 10, 20),
                getResultOfExitVeloVsLaunchAngle(atBats, 20, 30),
                getResultOfExitVeloVsLaunchAngle(atBats, 30, 40),
                getResultOfExitVeloVsLaunchAngle(atBats, 40, 100)
        );

        return set;
    }

    public SprayChart generateSprayChart(List<AtBat> atBats) {

        List<AtBat> ballsInRange = atBats
                .stream()
                .filter(atBat -> atBat.getHorizontalAngle() >= -45 && atBat.getHorizontalAngle() <= 45)
                .collect(Collectors.toList());

        AngleRange leftAngle = new AngleRange(-45, -15);
        AngleRange centerAngle = new AngleRange(-15, 15);
        AngleRange rightAngle = new AngleRange(15, 45);

        SprayChartDataResult left = getSprayChartDataResult(ballsInRange, leftAngle);
        SprayChartDataResult center = getSprayChartDataResult(ballsInRange, centerAngle);
        SprayChartDataResult right = getSprayChartDataResult(ballsInRange, rightAngle);

        return new SprayChart(left, center, right);
    }

    public Double getAvgHhbLaunchAngle(List<AtBat> atBats) {
        List<AtBat> hhbAtBats = atBats
                .stream()
                .filter(atBat -> atBat.getExitVelocity() > 80)
                .collect(Collectors.toList());
        return getAvgVertAngle(hhbAtBats);
    }

    public Double getLaStdDeviation(List<AtBat> atBats) {
        Double average = atBats
                .stream()
                .mapToInt(AtBat::getVerticalAngle)
                .summaryStatistics()
                .getAverage();

        Double avgOfDifferenceOfMeanAndElements= atBats
                .stream()
                .mapToDouble(atBat -> Math.pow((atBat.getVerticalAngle() - average), 2 ))
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
