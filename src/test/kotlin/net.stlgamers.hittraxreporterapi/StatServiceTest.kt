package net.stlgamers.hittraxreporterapi

import net.stlgamers.hittraxreporterapi.models.AtBat
import net.stlgamers.hittraxreporterapi.models.ZoneData
import net.stlgamers.hittraxreporterapi.services.StatService
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.stream.Collectors

class StatServiceTest {

    val statService = StatService()

    @Test
    fun givenAtBats_whenGenerateStrikeZoneData_thenStrikeZoneDataGenerated() {
        val atBat1 = generateTestAtBatByEvLaAndZone(55.0, 10, 3)
        val atBat2 = generateTestAtBatByEvLaAndZone(55.0, 30, 3)
        val testAtBats = Arrays.asList<AtBat>(atBat1, atBat2)

        val actualZoneData = statService.generateStrikeZoneData(testAtBats)

        val actualPos1Data = actualZoneData
                .filter {zoneData -> zoneData.position == ZoneData.Zone.TOP_LEFT}
                .first()

        Assertions.assertThat(Optional.of(55.0).get()).isEqualTo(actualPos1Data.avgExitVelocity)
        Assertions.assertThat(Optional.of(20).get()).isEqualTo(actualPos1Data.avgLaunchAngle)
    }

    @Test
    fun genPitchVeloData_withValidData() {
        val atBat1 = generateTestAtBatByEvLaAndPitchVelo(50.0, 10, 55.0)
        val atBat2 = generateTestAtBatByEvLaAndPitchVelo(50.0, 10, 62.0)
        val atBat3 = generateTestAtBatByEvLaAndPitchVelo(50.0, 10, 67.0)
        val atBat4 = generateTestAtBatByEvLaAndPitchVelo(50.0, 10, 72.0)
        val atBat5 = generateTestAtBatByEvLaAndPitchVelo(50.0, 10, 78.0)

        val testAtBats = listOf(atBat1, atBat2, atBat3, atBat4, atBat5)

        val actualData = statService.generatePitchVeloData(testAtBats)
        Assertions.assertThat(actualData[0].avgEv).isEqualTo(atBat1.exitVelocity)
        Assertions.assertThat(actualData[1].avgEv).isEqualTo(atBat2.exitVelocity)
        Assertions.assertThat(actualData[2].avgEv).isEqualTo(atBat3.exitVelocity)
        Assertions.assertThat(actualData[3].avgEv).isEqualTo(atBat4.exitVelocity)
        Assertions.assertThat(actualData[4].avgEv).isEqualTo(atBat5.exitVelocity)
    }

    private fun generateTestAtBatByEvLaAndPitchVelo(ev: Double?, la: Int?, pv: Double?): AtBat {
        val atBat = AtBat()
        atBat.exitVelocity = ev
        atBat.verticalAngle = la
        atBat.pitchVelocity = pv
        return atBat
    }

    private fun generateTestAtBatByEvLaAndZone(ev: Double?, la: Int?, zone: Int?): AtBat {
        val atBat = AtBat()
        atBat.exitVelocity = ev
        atBat.verticalAngle = la
        atBat.strikeZonePosition = zone
        return atBat
    }

}
