package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Settings {
    @JsonProperty("data_rate")
    public DataRate dataRate = new DataRate();
    @JsonProperty("frequency")
    public int frequency;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{datarate=%s,frequency=%d}", dataRate, frequency);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DataRate {
        @JsonProperty("lora")
        public DataRate.Lora lora = new DataRate.Lora();

        @JsonProperty("fsk")
        public DataRate.Fsk fsk = new DataRate.Fsk();

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{lora=%s,fsk=%s}", lora, fsk);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Lora {
            @JsonProperty("spreading_factor")
            public int spreadingFactor;
            @JsonProperty("bandwidth")
            public int bandWidth;
            @JsonProperty("coding_rate")
            public String codingRate;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{SF=%d,BW=%d,CR=%s}", spreadingFactor, bandWidth, codingRate);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Fsk {
            @JsonProperty("bit_rate")
            public int bitRate;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{bitrate=%d}", bitRate);
            }
        }
    }
}
