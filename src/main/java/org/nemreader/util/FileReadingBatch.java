package org.nemreader.util;

import org.nemreader.model.db.MeterReading;
import org.nemreader.model.state.Nem12ParserState;
import org.nemreader.repository.db.MeterReadingRepository;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileReadingBatch {

    private static final int BATCH_SIZE = 1000;

    public FileReadingBatch() {

    }

    public boolean run(Path filePath, DataSource dataSource) {
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            System.out.println("Invalid file path: " + filePath);
        }

        MeterReadingRepository repository = new MeterReadingRepository(dataSource);
        List<MeterReading> batch = new ArrayList<>();
        Nem12ParserState parserState = new Nem12ParserState();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<MeterReading> readings = parserState.processLine(line);
                batch.addAll(readings);

                if (batch.size() >= BATCH_SIZE) {
                    try {
                        repository.saveAll(batch);
                    } catch (Exception e) {
                        System.err.println("Query save failed (batch): " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                try {
                    repository.saveAll(batch);
                } catch (Exception e) {
                    System.err.println("Query save failed (remaining): " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

        } catch (IOException e) {
            System.err.println("File read failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
