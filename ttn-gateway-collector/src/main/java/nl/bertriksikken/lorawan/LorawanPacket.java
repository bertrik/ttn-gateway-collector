package nl.bertriksikken.lorawan;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public final class LorawanPacket {

    public final int mhdr;
    public final MacPayload macPayload;
    public final byte[] mic;

    LorawanPacket(int mhdr, MacPayload macPayload, byte[] mic) {
        this.mhdr = mhdr;
        this.macPayload = macPayload;
        this.mic = mic;
    }

    public static LorawanPacket decode(byte[] phyPayload) throws IOException {
        try {
            ByteBuffer bb = ByteBuffer.wrap(phyPayload);
            int mhdr = bb.get() & 0xFF;
            int len = bb.remaining() - 4;
            if (len < 0) {
                throw new IOException("MAC payload too small to parse");
            }
            byte[] macPayloadData = new byte[len];
            bb.get(macPayloadData);
            MacPayload macPayload = MacPayload.decode(macPayloadData);
            byte[] mic = new byte[4];
            bb.get(mic);
            return new LorawanPacket(mhdr, macPayload, mic);
        } catch (BufferUnderflowException e) {
            throw new IOException("Caught BufferUnderflowException", e);
        }
    }

    public static final class MacPayload {

        public final String devAddr;
        public final boolean adr;
        public final boolean ack;
        public final int fcnt;
        public final int fport;
        public final byte[] frmPayload;

        MacPayload(String devAddr, boolean adr, boolean ack, int fcnt, int fport, byte[] frmPayload) {
            this.devAddr = devAddr;
            this.adr = adr;
            this.ack = ack;
            this.fcnt = fcnt;
            this.fport = fport;
            this.frmPayload = frmPayload;
        }

        private static MacPayload decode(byte[] macPayloadData) {
            ByteBuffer bb = ByteBuffer.wrap(macPayloadData).order(ByteOrder.LITTLE_ENDIAN);
            // FHDR
            int devAddr = bb.getInt();
            String devAddrHex = String.format(Locale.ROOT, "%08X", devAddr);
            byte fctrl = bb.get();
            boolean adr = (fctrl & 0x80) != 0;
            boolean ack = (fctrl & 0x20) != 0;
            int fcnt = bb.getShort() & 0xFFFF;
            int fport = 0;
            if (bb.remaining() > 0) {
                fport = bb.get() & 0xFF;
            }
            byte[] frmPayload = new byte[bb.remaining()];
            bb.get(frmPayload);
            return new MacPayload(devAddrHex, adr, ack, fcnt, fport, frmPayload);
        }
    }
}
