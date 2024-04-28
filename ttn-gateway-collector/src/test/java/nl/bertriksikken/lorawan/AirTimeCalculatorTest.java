package nl.bertriksikken.lorawan;

import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.Assert;
import org.junit.Test;

public final class AirTimeCalculatorTest {

    private final AirTimeCalculator calculator = AirTimeCalculator.LORAWAN;

    /**
     * Verifies some air time values, in comparison to
     * https://www.thethingsnetwork.org/airtime-calculator<br>
     * The link above already takes into account the lorawan overhead (12 bytes).
     */
    @Test
    public void testLoRa() {
        assertAirTime(77.1, 7, 20);
        assertAirTime(133.6, 8, 20);
        assertAirTime(246.8, 9, 20);
        assertAirTime(452.6, 10, 20);
        assertAirTime(987.1, 11, 20);
        assertAirTime(1810.4, 12, 20);
    }

    private void assertAirTime(double expected, int sf, int n) {
        UplinkMessage uplink = new UplinkMessage();
        uplink.rawPayload = new byte[12 + n];
        uplink.settings.dataRate.lora.spreadingFactor = sf;
        double ms = calculator.calculate(uplink) / 0.001;
        Assert.assertEquals(expected, ms, 0.1);
    }

    /**
     * Verifies FSK air time calculation, don't actually know the reference value.
     */
    @Test
    public void testFsk() {
        assertFskAirTime(6.88, 50_000, 20);
    }

    private void assertFskAirTime(double expected, int bitRate, int n) {
        UplinkMessage uplink = new UplinkMessage();
        uplink.rawPayload = new byte[12 + n];
        uplink.settings.dataRate.fsk.bitRate = bitRate;
        double ms = calculator.calculate(uplink) / 0.001;
        Assert.assertEquals(expected, ms, 0.1);
    }

}
