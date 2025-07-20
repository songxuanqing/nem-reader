package org.nemreader.repository.db;

import org.nemreader.model.db.MeterReading;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class MeterReadingRepository {
    private final DataSource dataSource;

    public MeterReadingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void saveAll(List<MeterReading> readings) throws SQLException {
        String sql = "INSERT INTO meter_readings (nmi, timestamp, consumption) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (MeterReading reading : readings) {
                ps.setString(1, reading.getNmi());
                ps.setTimestamp(2, Timestamp.valueOf(reading.getTimestamp()));
                ps.setBigDecimal(3, BigDecimal.valueOf(reading.getConsumption()));
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new SQLException("SQLException : ");
        }
    }
}
