package nl.bertriksikken.lorawan;

/**
 * Air time calculation, according to AN1200.13 "LoRa Modem Designer’s Guide" 
 */
public final class AirTimeCalculator {
	
	// air time calculator for typical settings used in LoRaWAN
	public static AirTimeCalculator LORAWAN = new AirTimeCalculator(8, true, 125_000, 1);
	
	private int bw;
	private int preamble;
	private boolean header;
	private int cr;

	public AirTimeCalculator(int preamble, boolean header, int bw, int cr) {
		this.preamble = preamble;
		this.header = header;
		this.bw = bw;
		this.cr = cr;
	}
	
	public double calculate(int sf, int pl) {
		double tsym = Math.pow(2, sf) / bw;
		double tpreamble = tsym * (preamble + 4.25);
		boolean de = (sf >= 11);
		double numerator = (8 * pl) - (4 * sf) + 28 + 16 + (header ? 20 : 0);
		double denominator = 4 * (sf - (de ? 2 : 0));
		int nsymbols = 8 + (int)Math.max(Math.ceil(numerator / denominator) * (cr + 4), 0);
		double tpayload = tsym * nsymbols;
		return tpreamble + tpayload;
	}
	
}
