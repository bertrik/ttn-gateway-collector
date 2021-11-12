package nl.bertriksikken.ttngatewaycollector;

import com.google.common.base.Objects;

/**
 * Key to use for duplication detection.<br>
 * A message is considered duplicate if we receive consecutive packets, that
 * have identical raw data, are on the same frequency and for the same gateway.
 */
public final class GatewayFrequencyKey {

    private final String gatewayId;
    private final long frequency;

    GatewayFrequencyKey(String gatewayId, long frequency) {
        this.gatewayId = gatewayId;
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof GatewayFrequencyKey) {
            GatewayFrequencyKey other = (GatewayFrequencyKey) object;
            return gatewayId.equals(other.gatewayId) && frequency == other.frequency;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gatewayId, frequency);
    }

}
