package nl.bertriksikken.lorawan;

import java.util.stream.Stream;

public enum MType {

    JOIN_REQUEST(0, "JOIN_REQUEST"), //
    JOIN_ACCEPT(1, "JOIN_ACCEPT"), //
    UNCONFIRMED_DATA_UP(2, "UNCONFIRMED_UP"), //
    UNCONFIRMED_DATA_DOWN(3, "UNCONFIRMED_DOWN"), //
    CONFIRMED_DATA_UP(4, "CONFIRMED_UP"), //
    CONFIRMED_DATA_DOWN(5, "CONFIRMED_DOWN"), //
    RFU(6, "RFU"), //
    PROPRIETARY(7, "PROPRIETARY");

    private final int code;
    private final String description;

    /**
     * @param code        the code according to the specification
     * @param description the description according to TTN
     */
    MType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static MType fromMhdr(int mhdr) {
        int code = (mhdr >> 5) & 7;
        return Stream.of(MType.values()).filter(v -> (v.code == code)).findFirst().orElseThrow();
    }

    @Override
    public String toString() {
        return description;
    }

}
