package nl.bertriksikken.ttngatewaycollector.export;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.EntityIdentifiers;
import nl.bertriksikken.ttn.lorawan.v3.GatewayStatus;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.IEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ExportEventWriter implements IEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ExportEventWriter.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final CsvMapper csvMapper = new CsvMapper();
    private final File logFile;

    public ExportEventWriter(CsvLoggerConfig config) {
        logFile = new File(config.logFileName);
        csvMapper.findAndRegisterModules();
        csvMapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
    }

    void write(ExportEvent exportEvent) {
        boolean append = logFile.exists();
        CsvSchema schema = csvMapper.schemaFor(exportEvent.getClass());
        schema = append ? schema.withoutHeader() : schema.withHeader();
        ObjectWriter writer = csvMapper.writer(schema);
        try (FileOutputStream fos = new FileOutputStream(logFile, append)) {
            writer.writeValue(fos, exportEvent);
        } catch (IOException e) {
            LOG.warn("Caught exception writing to file", e);
        }
    }

    @Override
    public void handleUplink(UplinkMessage uplink) {
        ExportEvent event = ExportEvent.fromUplinkMessage(uplink);
        executor.execute(() -> write(event));
    }

    @Override
    public void handleDownlink(Instant time, String gateway, DownlinkMessage downlink) {
        ExportEvent event = ExportEvent.fromDownlinkData(time, gateway, downlink);
        executor.execute(() -> write(event));
    }

    @Override
    public void handleStatus(Instant time, EntityIdentifiers.GatewayIdentifiers gatewayIds, GatewayStatus gatewayStatus) {
        // not implemented
    }

    @Override
    public void start() {
        LOG.info("Starting CVS event writer for '{}'", logFile.getAbsolutePath());
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

}
