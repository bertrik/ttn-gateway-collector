package nl.bertriksikken.lorawan;

import nl.bertriksikken.ttn.lorawan.v3.Settings.DataRate;
import nl.bertriksikken.ttn.lorawan.v3.Settings.DataRate.Lora;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;

/**
 * Air time calculation, according to AN1200.13 "LoRa Modem Designer’s Guide"
 */
public final class AirTimeCalculator {

    // air time calculator for typical settings used in LoRaWAN
    public static final AirTimeCalculator LORAWAN = new AirTimeCalculator(8, true, 125_000, 1);

    private final int bw;
    private final int preamble;
    private final boolean header;
    private final int cr;

    public AirTimeCalculator(int preamble, boolean header, int bw, int cr) {
        this.preamble = preamble;
        this.header = header;
        this.bw = bw;
        this.cr = cr;
    }

    public double calculate(DataRate dataRate, int pl) {
        // LoRa
        Lora lora = dataRate.lora();
        int sf = (lora != null) ? dataRate.lora().spreadingFactor() : 0;
        if ((sf >= 6) && (sf <= 12)) {
            double tsym = Math.pow(2, sf) / bw;
            double tpreamble = tsym * (preamble + 4.25);
            boolean de = (sf >= 11);
            double numerator = (8 * pl) - (4 * sf) + 28 + 16 + (header ? 20 : 0);
            double denominator = 4 * (sf - (de ? 2 : 0));
            int nsymbols = 8 + (int) Math.max(Math.ceil(numerator / denominator) * (cr + 4), 0);
            double tpayload = tsym * nsymbols;
            return tpreamble + tpayload;
        }
        // FSK
        DataRate.Fsk fsk = dataRate.fsk();
        int br = (fsk != null) ? dataRate.fsk().bitRate() : 0;
        if (br > 0) {
            // according
            // https://lora-alliance.org/wp-content/uploads/2021/05/RP002-1.0.3-FINAL-1.pdf
            // paragraph 4.2.1
            int nbits = (11 + pl) * 8;
            return (double) nbits / br;
        }
        // can't calculate
        return 0.0;
    }

    public double calculate(UplinkMessage message) {
        return calculate(message.settings().dataRate(), message.rawPayload().length);
    }

}
