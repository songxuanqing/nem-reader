package org.nemreader.util;

import java.time.LocalDateTime;

public class IntervalReading {
    public final String nmi;
    public final LocalDateTime timestamp;
    public final double value;

    public IntervalReading(String nmi, LocalDateTime timestamp, double value) {
        this.nmi = nmi;
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "NMI=" + nmi + ", Time=" + timestamp + ", Value=" + value;
    }

    public String getNmi() {
        return nmi;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }
}