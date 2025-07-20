package org.nemreader.model.csv;

import java.time.LocalDateTime;

public class Nem12Header100 {
    // Header record indicator. Always set to 100 and must appear once per file.
    // Each 100 record must have a matching 900 record.
    private int recordIndicator;

    // Version identifier indicating the format version of the data block.
    // Allowed value: "NEM12"
    private String versionHeader;

    // File creation date and time in 12-digit format (e.g., YYYYMMDDHHMM).
    private LocalDateTime dateTime;

    // Participant ID of the Meter Data Provider (MDP) generating the file.
    private String fromParticipant;

    // Participant ID of the recipient of the file.
    private String toParticipant;

    public Nem12Header100(int r, String v, LocalDateTime dt, String from, String to) {
        this.recordIndicator = r;
        this.versionHeader = v;
        this.dateTime = dt;
        this.fromParticipant = from;
        this.toParticipant = to;
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public String getVersionHeader() {
        return versionHeader;
    }

    public void setVersionHeader(String versionHeader) {
        this.versionHeader = versionHeader;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getFromParticipant() {
        return fromParticipant;
    }

    public void setFromParticipant(String fromParticipant) {
        this.fromParticipant = fromParticipant;
    }

    public String getToParticipant() {
        return toParticipant;
    }

    public void setToParticipant(String toParticipant) {
        this.toParticipant = toParticipant;
    }
}