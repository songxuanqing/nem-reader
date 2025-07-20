package org.nemreader.model.csv;

import java.time.LocalDateTime;

public class Nem12B2BDetails500 {
    // B2B details record indicator. Must always be 500.
    private int recordIndicator;

    // Transaction code indicating the reason the recipient is receiving this metering data.
    // Allowed values are defined in Appendix A. Use 'O' for Historical Data or if the reason is unknown.
    private String transCode;

    // Service Order number associated with the meter reading.
    // Required field.
    private String retServiceOrder;

    // Date and time when the meter reading was taken or should have occurred.
    // Required for Historical Data; not required for Estimates.
    // If actual time is unavailable, use "00:00:01" as the time component.
    private LocalDateTime readDateTime;

    // Total accumulated energy recorded by the meter at the time of the reading.
    // Required for Type 4A and Type 5 installations when collected.
    private double indexRead;


    public Nem12B2BDetails500(int r, String code, String order, LocalDateTime dt, double index) {
        this.recordIndicator = r;
        this.transCode = code;
        this.retServiceOrder = order;
        this.readDateTime = dt;
        this.indexRead = index;
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getRetServiceOrder() {
        return retServiceOrder;
    }

    public void setRetServiceOrder(String retServiceOrder) {
        this.retServiceOrder = retServiceOrder;
    }

    public LocalDateTime getReadDateTime() {
        return readDateTime;
    }

    public void setReadDateTime(LocalDateTime readDateTime) {
        this.readDateTime = readDateTime;
    }

    public double getIndexRead() {
        return indexRead;
    }

    public void setIndexRead(double indexRead) {
        this.indexRead = indexRead;
    }
}