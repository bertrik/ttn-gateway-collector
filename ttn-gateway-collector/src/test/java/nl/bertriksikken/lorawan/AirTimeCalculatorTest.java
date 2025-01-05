package nl.bertriksikken.lorawan;

import nl.bertriksikken.ttn.lorawan.v3.Settings;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class AirTimeCalculatorTest {

    private final AirTimeCalculator calculator = AirTimeCalculator.LORAWAN;

    /**
     * Verifies some air time values, in comparison to
     * <a href="https://www.thethingsnetwork.org/airtime-calculator">airtime calculator</a><br>
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
        Settings.DataRate.Lora lora = new Settings.DataRate.Lora(sf, 125000, "5/6");
        byte[] rawPayload = new byte[12 + n];
        UplinkMessage.Payload payload = new UplinkMessage.Payload();
        Settings settings = new Settings(new Settings.DataRate(lora), 868100000);
        UplinkMessage uplink = new UplinkMessage(rawPayload, payload, settings, List.of());
        double ms = calculator.calculate(uplink) / 0.001;
        Assertions.assertEquals(expected, ms, 0.1);
    }

    /**
     * Verifies FSK air time calculation, don't actually know the reference value.
     */
    @Test
    public void testFsk() {
        assertFskAirTime(6.88, 50_000, 20);
    }

    private void assertFskAirTime(double expected, int bitRate, int n) {
        byte[] rawPayload = new byte[12 + n];
        UplinkMessage.Payload payload = new UplinkMessage.Payload();
        Settings settings = new Settings(new Settings.DataRate(new Settings.DataRate.Fsk(bitRate)), 868100000);
        UplinkMessage uplink = new UplinkMessage(rawPayload, payload, settings, List.of());
        double ms = calculator.calculate(uplink) / 0.001;
        Assertions.assertEquals(expected, ms, 0.1);
    }

}
