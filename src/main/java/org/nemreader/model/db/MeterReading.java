package org.nemreader.model.db;

import java.time.LocalDateTime;

public class MeterReading {
    private String nmi;
    private LocalDateTime timestamp;
    private double consumption;

    public MeterReading(String nmi, LocalDateTime timestamp, double consumption) {
        this.nmi = nmi;
        this.timestamp = timestamp;
        this.consumption = consumption;
    }

    public String getNmi() {
        return nmi;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getConsumption() {
        return consumption;
    }
}
