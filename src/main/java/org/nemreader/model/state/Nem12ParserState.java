package org.nemreader.model.state;

import org.nemreader.model.csv.*;
import org.nemreader.model.db.MeterReading;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Nem12ParserState {
    private Nem12Header100 header;
    private Nem12End900 footer;
    private List<Nem12NMIDataDetails200> dataList = new ArrayList<>();
    private Nem12NMIDataDetails200 currentNMIData;

    public List<MeterReading> processLine(String line) {
        String[] tokens = line.split(",");
        String recordType = tokens[0];

        switch (recordType) {
            case "100":
                header = new Nem12Header100(
                        100,
                        tokens[1],
                        LocalDateTime.parse(tokens[2], DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
                        tokens[3],
                        tokens[4]
                );
                break;

            case "200":
                if (currentNMIData != null) {
                    dataList.add(currentNMIData);
                }
                currentNMIData = new Nem12NMIDataDetails200(
                        200,
                        tokens[1], tokens[2], tokens[3], tokens[4],
                        tokens[5], tokens[6], tokens[7],
                        String.valueOf(Integer.parseInt(tokens[8])),
                        tokens.length > 9 ? String.valueOf(LocalDate.parse(tokens[9], DateTimeFormatter.BASIC_ISO_DATE)) : null
                );
                break;

            case "300":
                List<Double> intervalValues = new ArrayList<>();
                int intervalLength = currentNMIData.getIntervalLength();
                int maxLength = 1440 / intervalLength;
                // Length of double values in 300 record: tokens.length - 5 (last 5 are quality, codes, etc.)
                for (int i = 2; i < 2 + maxLength; i++) {
                    try {
                        intervalValues.add(Double.parseDouble(tokens[i]));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid double in 300 record: " + tokens[i]);
                    }
                }

                int reasonCode = 0;
                String reasonCodeStr = tokens[tokens.length - 3];
                if (reasonCodeStr != null && !reasonCodeStr.isBlank()) {
                    try {
                        reasonCode = Integer.parseInt(reasonCodeStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Reason code parsing error: " + reasonCodeStr + ", using 0");
                    }
                }

                Nem12IntervalData300 intervalData = new Nem12IntervalData300(
                        300,
                        LocalDate.parse(tokens[1], DateTimeFormatter.BASIC_ISO_DATE),
                        intervalValues,
                        tokens[tokens.length - 4],                  // quality method
                        reasonCode,
                        tokens[tokens.length - 2],                  // reason description
                        parseDateTime(tokens[tokens.length - 1]),  // dateTime
                        null                                        // additional fields if any
                );
                currentNMIData.addIntervalData(intervalData);

                // Convert intervalValues in 300 record to MeterReading and return
                return convertIntervalDataToMeterReadings(intervalData);

            case "400":
                int startInterval = Integer.parseInt(tokens[1]);
                int endInterval = Integer.parseInt(tokens[2]);
                String qualityMethod = tokens[3];

                Integer reasonCode400 = null;
                String reasonDescription400 = null;

                if (tokens.length > 4 && !tokens[4].isBlank()) {
                    try {
                        reasonCode400 = Integer.parseInt(tokens[4]);
                    } catch (NumberFormatException e) {
                        System.err.println("400 record reasonCode parsing failed: " + tokens[4]);
                    }
                }

                if (tokens.length > 5) {
                    reasonDescription400 = tokens[5];
                }

                currentNMIData.addIntervalEvent(new Nem12IntervalEvent400(
                        400,
                        startInterval,
                        endInterval,
                        qualityMethod,
                        (reasonCode400 != null) ? reasonCode400 : -1,
                        reasonDescription400
                ));
                break;

            case "500":
                String token1 = tokens.length > 1 ? tokens[1] : null;
                String token2 = tokens.length > 2 ? tokens[2] : null;
                String token3 = tokens.length > 3 ? tokens[3] : null;
                String token4 = tokens.length > 4 ? tokens[4] : null;

                LocalDateTime parsedDate = null;
                if (token3 != null && !token3.isEmpty()) {
                    try {
                        parsedDate = parseDateTime(token3);
                    } catch (DateTimeParseException e) {
                        System.err.println("Date parsing failed: " + token3);
                    }
                }

                Double value = 0.0;
                if (token4 != null && !token4.isEmpty()) {
                    try {
                        value = Double.parseDouble(token4);
                    } catch (NumberFormatException e) {
                        System.err.println("Number parsing failed: " + token4);
                    }
                }

                currentNMIData.addB2BDetail(new Nem12B2BDetails500(
                        500,
                        token1,
                        token2,
                        parsedDate,
                        value
                ));
                break;

            case "900":
                footer = new Nem12End900(900);
                break;

            default:
                System.err.println("Unknown record type: " + recordType);
        }

        return List.of(); // No MeterReading except for 300 records
    }

    private List<MeterReading> convertIntervalDataToMeterReadings(Nem12IntervalData300 intervalData) {
        List<MeterReading> readings = new ArrayList<>();
        if (currentNMIData == null) {
            System.err.println("currentNMIData is null in convertIntervalDataToMeterReadings");
            return readings;
        }

        int intervalLength;
        try {
            intervalLength = currentNMIData.getIntervalLength();
        } catch (NumberFormatException e) {
            System.err.println("Invalid intervalLength: " + currentNMIData.getIntervalLength());
            intervalLength = 30;
        }

        LocalDate baseDate = intervalData.getIntervalDate();
        List<Double> values = intervalData.getIntervalValues();

        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i);
            if (Double.isNaN(value)) continue;

            LocalDateTime timestamp = baseDate.atStartOfDay().plusMinutes((long) intervalLength * i);
            // TODO: should use suffix data because there might be same NMI and date but different suffix (electricity resource)
            String nmiWithSuffix = currentNMIData.getNmi() + "_" + currentNMIData.getNmiSuffix();
            readings.add(new MeterReading(currentNMIData.getNmi(), timestamp, value));
        }

        return readings;
    }

    public Nem12File getParsedResult() {
        if (currentNMIData != null) {
            dataList.add(currentNMIData);
            currentNMIData = null;
        }
        return new Nem12File(header, dataList, footer);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        } catch (Exception e) {
            System.err.println("DateTime parse error: " + dateTimeStr);
            return null;
        }
    }
}
