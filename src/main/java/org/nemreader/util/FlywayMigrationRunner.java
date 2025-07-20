package org.nemreader.util;

import org.flywaydb.core.Flyway;

import java.sql.*;
import java.util.Scanner;

import org.nemreader.config.Config;

public class FlywayMigrationRunner {
    private final boolean isDockerRunning;

    public FlywayMigrationRunner(boolean isDockerRunning) {
        this.isDockerRunning = isDockerRunning;
    }

    public void run() {
        String host;
        String port;
        String appDb;
        String appUser;
        String appPass;
        boolean isMix;
        String appDbUrl;

        if (!isDockerRunning) {

            host = Config.get("postgres.host");
            port = Config.get("postgres.port");
            appDb = Config.get("app.db.name");
            appUser = Config.get("app.db.user");
            appPass = Config.get("app.db.password");
            isMix = Boolean.parseBoolean(Config.get("flyway.mix"));
            appDbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, appDb);

            boolean appDbAccessible = false;

            // (0) Test connection to app_db with app_user
            try (Connection conn = DriverManager.getConnection(appDbUrl, appUser, appPass)) {
                appDbAccessible = true;
                System.out.println("✔ Connected to app_db as app_user, skipping user and DB creation");
            } catch (SQLException e) {
                System.out.println("✖ Failed to connect to app_db as app_user, proceeding to create user and DB");
            }

            if (!appDbAccessible) {
                // (1) Flyway migration - create user and database
                if (!appDbAccessible) {
                    Scanner scanner = new Scanner(System.in);

                    System.out.print("PostgreSQL superuser ID: ");
                    String superUser = scanner.nextLine();

                    System.out.print("PostgreSQL superuser Password: ");
                    String superPass = scanner.nextLine();

                    Flyway flyway1 = Flyway.configure()
                            .dataSource(String.format("jdbc:postgresql://%s:%s/postgres", host, port), superUser, superPass)
                            .locations("classpath:db/migration/postgres")
                            .mixed(isMix)
                            .load();
                    flyway1.repair();
                    flyway1.migrate();

                    // (2) Check if app_db exists, create if missing
                    createDatabaseIfNotExists(host, port, "postgres", superUser, superPass, appDb, appUser);

                    scanner.close();
                }
            }
        } else {
            // In Docker environment, fix DB host to Docker service name (db)
            host = System.getenv("DB_HOST");
            port = System.getenv("DB_PORT");
            appDb = System.getenv("DB_NAME");
            appUser = System.getenv("DB_USER");
            appPass = System.getenv("DB_PASSWORD");
            isMix = Boolean.parseBoolean(System.getenv("FLYWAY_MIX"));
            appDbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, appDb);
        }

        // (2) Run migration on app_db
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(appDbUrl, appUser, appPass)
                    .locations("classpath:db/migration/appdb")
                    .mixed(isMix)
                    .load();

            flyway.repair();
            flyway.migrate();

            System.out.println("✅ Flyway migration complete (Host mode).");

        } catch (Exception e) {
            System.err.println("❌ Flyway migration failed (Host mode):");
            e.printStackTrace();
        }
    }


    private void createDatabaseIfNotExists(String host, String port, String adminDb, String adminUser, String adminPass, String targetDb, String ownerUser) {
        String adminUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, adminDb);

        try (Connection conn = DriverManager.getConnection(adminUrl, adminUser, adminPass);
             PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {

            checkStmt.setString(1, targetDb);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.printf("Database '%s' does not exist. Creating...%n", targetDb);
                try (Statement stmt = conn.createStatement()) {
                    String sql = String.format("CREATE DATABASE %s OWNER %s", targetDb, ownerUser);
                    stmt.executeUpdate(sql);
                    System.out.println("Database created successfully.");
                }
            } else {
                System.out.printf("Database '%s' already exists.%n", targetDb);
            }

        } catch (SQLException e) {
            System.err.printf("Error checking or creating database '%s': %s%n", targetDb, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getEnv(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }
}
