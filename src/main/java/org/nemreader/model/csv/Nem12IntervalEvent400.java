package org.nemreader.model.csv;

public class Nem12IntervalEvent400 {
    // Interval event record indicator. Must always be 400.
    private int recordIndicator;

    // The first interval number to which the QualityMethod and ReasonCode apply.
    // Must be less than or equal to endInterval.
    private int startInterval;

    // The last interval number to which the QualityMethod and ReasonCode apply.
    private int endInterval;

    // Quality and substitution/estimation method applied to the interval range.
    // Format: QMM (e.g., 'A', 'E01').
    // The 'V' quality flag is not permitted in this record.
    private String qualityMethod;

    // Reason code explaining substitution/estimation for the interval range.
    // Optional if quality flag is 'E'; mandatory in other cases.
    // Refer to Appendix E for valid codes.
    private int reasonCode;

    // Description of the reason code.
    // Mandatory when reasonCode is '0'; otherwise optional.
    private String reasonDescription;

    public Nem12IntervalEvent400(int r, int start, int end, String q, int code, String desc) {
        this.recordIndicator = r;
        this.startInterval = start;
        this.endInterval = end;
        this.qualityMethod = q;
        this.reasonCode = code;
        this.reasonDescription = desc;
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public int getStartInterval() {
        return startInterval;
    }

    public void setStartInterval(int startInterval) {
        this.startInterval = startInterval;
    }

    public int getEndInterval() {
        return endInterval;
    }

    public void setEndInterval(int endInterval) {
        this.endInterval = endInterval;
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
}