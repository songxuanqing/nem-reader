package org.nemreader.model.csv;

public class Nem12End900 {
    // This is the end of record indicator for the record set
    // commencing with the previous 100 record.
    // Allowed Value: 900
    private int recordIndicator;

    public Nem12End900(int r) {
        this.recordIndicator = r;
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }
}
