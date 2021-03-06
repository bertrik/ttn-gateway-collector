package nl.bertriksikken.ttngatewaycollector.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import nl.bertriksikken.ttn.message.UplinkMessage;

public final class ExportEventWriter {

    private final CsvMapper csvMapper = new CsvMapper();
    private final File logFile;
    
    public ExportEventWriter(File logFile) {
        this.logFile = logFile;
        csvMapper.findAndRegisterModules();
        csvMapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
    }

    public void write(UplinkMessage uplinkMessage) throws IOException {
        // create export event
        ExportEvent exportEvent = ExportEvent.fromUplinkMessage(uplinkMessage);

        // log it
        boolean append = logFile.exists();
        CsvSchema schema = csvMapper.schemaFor(exportEvent.getClass());
        schema = append ? schema.withoutHeader() : schema.withHeader();
        ObjectWriter writer = csvMapper.writer(schema);
        try (FileOutputStream fos = new FileOutputStream(logFile, append)) {
            writer.writeValue(fos, exportEvent);
        }
    }

}
