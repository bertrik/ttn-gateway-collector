package nl.bertriksikken.lorawan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class LorawanPacketTest {

    /**
     * Example: https://lorawan-packet-decoder-0ta6puiniaut.runkit.sh/?data=6058170b268024027886141b
     */
    @Test
    public void test() throws IOException {
        byte[] data = new byte[] { 0x60, 0x58, 0x17, 0x0b, 0x26, (byte) 0x80, 0x24, 0x02, 0x78, (byte) 0x86, 0x14,
            0x1b };
        LorawanPacket packet = LorawanPacket.decode(data);
        Assertions.assertEquals(MType.UNCONFIRMED_DATA_DOWN, MType.fromMhdr(packet.mhdr));
        Assertions.assertEquals("260B1758", packet.macPayload.devAddr);
        Assertions.assertTrue(packet.macPayload.adr);
        Assertions.assertFalse(packet.macPayload.ack);
        Assertions.assertEquals(548, packet.macPayload.fcnt);
        Assertions.assertEquals(0, packet.macPayload.fport);
        Assertions.assertEquals(0, packet.macPayload.frmPayload.length);
    }

}
