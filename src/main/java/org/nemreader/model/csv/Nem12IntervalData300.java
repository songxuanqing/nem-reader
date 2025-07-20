package org.nemreader.model.csv;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Nem12IntervalData300 {
    // Interval metering data record indicator. Must always be 300.
    private int recordIndicator;

    // The date for which the interval data applies (format: YYYYMMDD).
    private LocalDate intervalDate;

    // List of interval values for the specified date.
    // The number of values must equal 1440 divided by the IntervalLength (e.g., 48 for 30-min intervals).
    // Each value represents energy or measured quantity for one interval.
    // Must be non-negative; decimals allowed; exponential format not allowed.
    private List<Double> intervalValues;

    // Summary of the quality and estimation/substitution method applied to all interval values in this record.
    // Format: QMM (e.g., 'A', 'E01', 'V').
    // 'V' is used if multiple quality methods or reason codes apply.
    private String qualityMethod;

    // Reason code indicating why substitution/estimation was applied (if any).
    // Optional if quality flag is 'A' or 'E', but required in other cases.
    // Must not be populated if quality flag is 'V'.
    private int reasonCode;

    // Description of the reason code.
    // Mandatory if reason code is '0'; otherwise optional.
    private String reasonDescription;

    // Timestamp when the interval values or quality method were last updated by the MDP
    // Represents the MDP's version of the data for this date.
    private LocalDateTime updateDateTime;

    // Timestamp recorded by MSATS when the data was loaded into MSATS.
    // Appears in the acknowledgement sent back to the MDP.
    private LocalDateTime msatsLoadDateTime;

    public Nem12IntervalData300(int r, LocalDate d, List<Double> values, String q, int reasonCode, String reasonDesc, LocalDateTime update, LocalDateTime msats) {
        this.recordIndicator = r;
        this.intervalDate = d;
        this.intervalValues = values;
        this.qualityMethod = q;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDesc;
        this.updateDateTime = update;
        this.msatsLoadDateTime = msats;
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public LocalDate getIntervalDate() {
        return intervalDate;
    }

    public void setIntervalDate(LocalDate intervalDate) {
        this.intervalDate = intervalDate;
    }

    public List<Double> getIntervalValues() {
        return intervalValues;
    }

    public void setIntervalValues(List<Double> intervalValues) {
        this.intervalValues = intervalValues;
    }

    public String getQualityMethod() {
        return qualityMethod;
    }

    public void setQualityMethod(String qualityMethod) {
        this.qualityMethod = qualityMethod;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public LocalDateTime getMsatsLoadDateTime() {
        return msatsLoadDateTime;
    }

    public void setMsatsLoadDateTime(LocalDateTime msatsLoadDateTime) {
        this.msatsLoadDateTime = msatsLoadDateTime;
    }
}