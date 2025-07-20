package org.nemreader.model.csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Nem12NMIDataDetails200 {
    // NMI data details record indicator. Must always be 200.
    private int recordIndicator;

    // NMI (National Metering Identifier) for the connection point.
    // Excludes check-digit and NMI suffix.
    private String nmi;

    // Concatenated string of all NMISuffixes applicable to the NMI.
    // Must represent the actual site configuration.
    private String nmiConfiguration;

    // Interval meter register identifier (e.g. "1", "2", "E1").
    // Mandatory for Type 4, 4A, and 5 when the sender is the current MDP.
    // Optional for Types 1-3, 7, or when sending to another MDP.
    private String registerId;

    // NMI suffix, defined in the NMI Procedure (e.g. "E1", "B1").
    private String nmiSuffix;

    // Data stream identifier, e.g., "N1", "N2".
    // Required if the data is or would be sent to MSATS.
    private String mdmDataStreamIdentifier;

    // Serial number of the meter installed at the site.
    // Optional for Type 7 meters or virtual/logical meters.
    private String meterSerialNumber;

    // Unit of measure for the interval data (e.g., kWh, kVarh).
    private String uom;

    // Length of each interval period, in minutes (allowed: 5, 15, or 30).
    private int intervalLength;

    // Next scheduled read date (NSRD).
    // Optional for remotely read meters or when meter will no longer be read.
    private LocalDate nextScheduledReadDate;

    // List of 300 record interval data lines associated with this NMI.
    private List<Nem12IntervalData300> intervalData = new ArrayList<>();

    // List of 400 record interval event lines associated with this NMI.
    private List<Nem12IntervalEvent400> intervalEvents = new ArrayList<>();

    // List of 500 record B2B detail lines associated with this NMI.
    private List<Nem12B2BDetails500> b2bDetails = new ArrayList<>();

    public Nem12NMIDataDetails200(int r, String... fields) {
        this.recordIndicator = r;
        this.nmi = fields[0];
        this.nmiConfiguration = fields[1];
        this.registerId = fields[2];
        this.nmiSuffix = fields[3];
        this.mdmDataStreamIdentifier = fields[4];
        this.meterSerialNumber = fields[5];
        this.uom = fields[6];
        this.intervalLength = Integer.parseInt(fields[7]);
        this.nextScheduledReadDate = (fields.length > 8 && fields[8] != null && !fields[8].isEmpty())
                ? LocalDate.parse(fields[8].replace("-",""), DateTimeFormatter.BASIC_ISO_DATE)
                : null;

    }



    public void addIntervalData(Nem12IntervalData300 d) { intervalData.add(d); }
    public void addIntervalEvent(Nem12IntervalEvent400 e) { intervalEvents.add(e); }
    public void addB2BDetail(Nem12B2BDetails500 b) { b2bDetails.add(b); }

    @Override
    public String toString() {
        return "NMI: " + nmi + ", Meter: " + meterSerialNumber + ", Register: " + registerId +
                ", Intervals: " + intervalData.size();
    }

    public int getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(int recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public String getNmi() {
        return nmi;
    }

    public void setNmi(String nmi) {
        this.nmi = nmi;
    }

    public String getNmiConfiguration() {
        return nmiConfiguration;
    }

    public void setNmiConfiguration(String nmiConfiguration) {
        this.nmiConfiguration = nmiConfiguration;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getNmiSuffix() {
        return nmiSuffix;
    }

    public void setNmiSuffix(String nmiSuffix) {
        this.nmiSuffix = nmiSuffix;
    }

    public String getMdmDataStreamIdentifier() {
        return mdmDataStreamIdentifier;
    }

    public void setMdmDataStreamIdentifier(String mdmDataStreamIdentifier) {
        this.mdmDataStreamIdentifier = mdmDataStreamIdentifier;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    public void setMeterSerialNumber(String meterSerialNumber) {
        this.meterSerialNumber = meterSerialNumber;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(int intervalLength) {
        this.intervalLength = intervalLength;
    }

    public LocalDate getNextScheduledReadDate() {
        return nextScheduledReadDate;
    }

    public void setNextScheduledReadDate(LocalDate nextScheduledReadDate) {
        this.nextScheduledReadDate = nextScheduledReadDate;
    }

    public List<Nem12IntervalData300> getIntervalData() {
        return intervalData;
    }

    public void setIntervalData(List<Nem12IntervalData300> intervalData) {
        this.intervalData = intervalData;
    }

    public List<Nem12IntervalEvent400> getIntervalEvents() {
        return intervalEvents;
    }

    public void setIntervalEvents(List<Nem12IntervalEvent400> intervalEvents) {
        this.intervalEvents = intervalEvents;
    }

    public List<Nem12B2BDetails500> getB2bDetails() {
        return b2bDetails;
    }

    public void setB2bDetails(List<Nem12B2BDetails500> b2bDetails) {
        this.b2bDetails = b2bDetails;
    }
}
