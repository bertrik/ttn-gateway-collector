package nl.bertriksikken.ttngatewaycollector.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CsvLoggerConfig {

    @JsonProperty("filename")
    public String logFileName = "gateway.csv";

}
