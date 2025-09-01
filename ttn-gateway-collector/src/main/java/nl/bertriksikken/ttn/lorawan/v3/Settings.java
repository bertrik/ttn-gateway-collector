package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Settings(@JsonProperty("data_rate") DataRate dataRate,
                       @JsonProperty("frequency") int frequency) {
    @SuppressWarnings("MissingOverride")
    public Settings() {
        this(new DataRate(new DataRate.Lora()), 0);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DataRate(@JsonProperty("lora") Lora lora, @JsonProperty("fsk") Fsk fsk) {
        public DataRate(Lora lora) {
            this(lora, new Fsk(0));
        }

        public DataRate(Fsk fsk) {
            this(new Lora(), fsk);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Lora(@JsonProperty("spreading_factor") int spreadingFactor,
                           @JsonProperty("bandwidth") int bandWidth, @JsonProperty("coding_rate") String codingRate) {
            Lora() {
                this(0, 0, "");
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Fsk(@JsonProperty("bit_rate") int bitRate) {
        }
    }
}
