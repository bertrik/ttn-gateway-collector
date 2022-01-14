package nl.bertriksikken.lorawan;

import java.util.Base64;

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
    public void testHappyFlow() {
        assertAirTime(77.1, 7, 20);
        assertAirTime(133.6, 8, 20);
        assertAirTime(246.8, 9, 20);
        assertAirTime(452.6, 10, 20);
        assertAirTime(987.1, 11, 20);
        assertAirTime(1810.4, 12, 20);
    }

    /**
     * Verifies that air time calculation with FSK doesn't result in a negative
     * value.
     */
    @Test
    public void testFsk() {
        assertAirTime(0.0, 0, 20);
    }

    private void assertAirTime(double expected, int sf, int n) {
        double ms = calculator.calculate(sf, 12 + n) / 0.001;
        Assert.assertEquals(expected, ms, 0.1);
    }

}
