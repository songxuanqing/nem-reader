package org.nemreader.util;

import java.io.*;
import java.util.regex.Pattern;

import org.nemreader.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Postgresql {
    private HikariDataSource dataSource;

    public void initDataSource(boolean isDocker) {
        if (dataSource != null) return; // Use existing if already initialized

        String host, port, dbName, user, password;

        if (isDocker) {
            // Use Docker environment variables
            host = System.getenv().getOrDefault("DB_HOST", "localhost");
            port = System.getenv().getOrDefault("DB_PORT", "5432");
            dbName = System.getenv().getOrDefault("DB_NAME", "nemdb");
            user = System.getenv().getOrDefault("DB_USER", "nemuser");
            password = System.getenv().getOrDefault("DB_PASSWORD", "nempass");
        } else {
            host = Config.get("postgres.host");
            port = Config.get("postgres.port");
            dbName = Config.get("app.db.name");
            user = Config.get("app.db.user");
            password = Config.get("app.db.password");
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    // Initialize to local environment by default if dataSource is null when getDataSource called
    public HikariDataSource getDataSource(boolean docker) {
        if (dataSource == null) {
            initDataSource(docker);
        }
        return dataSource;
    }

    public boolean isPostgresInstalled() {

        boolean isPostgresServiceRunning = false;
        boolean isPostgresInstalled = false;

        try {
            // 1. Check if any PostgreSQL service is in RUNNING state
            Process listServices = new ProcessBuilder("cmd.exe", "/c", "sc query state= all").start();
            BufferedReader serviceReader = new BufferedReader(new InputStreamReader(listServices.getInputStream()));

            String line;
            String currentService = null;
            boolean isPostgresService = false;

            Pattern servicePattern = Pattern.compile("^SERVICE_NAME:\\s+(postgresql.*?)\\s*$", Pattern.CASE_INSENSITIVE);

            while ((line = serviceReader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("SERVICE_NAME:")) {
                    var matcher = servicePattern.matcher(line);
                    if (matcher.find()) {
                        currentService = matcher.group(1);
                        isPostgresService = true;
                    } else {
                        isPostgresService = false;
                    }
                }

                if (isPostgresService && line.startsWith("STATE")) {
                    if (line.contains("RUNNING")) {
                        System.out.println("✔ Running PostgreSQL service detected: " + currentService);
                        isPostgresServiceRunning = true;
                        break; // No need to check further if one is running
                    }
                    isPostgresService = false;
                }
            }

            listServices.waitFor();
        } catch (Exception e) {
            System.err.println("Error checking services: " + e.getMessage());
        }

        // 2. If no running service, check PostgreSQL installation by 'psql' command
        if (!isPostgresServiceRunning) {
            try {
                Process checkPsql = new ProcessBuilder("psql", "--version").start();
                BufferedReader psqlReader = new BufferedReader(new InputStreamReader(checkPsql.getInputStream()));

                String versionOutput = psqlReader.readLine();
                if (versionOutput != null && versionOutput.toLowerCase().contains("psql")) {
                    System.out.println("✔ PostgreSQL installed (psql version detected): " + versionOutput);
                    isPostgresInstalled = true;
                }

                checkPsql.waitFor();
            } catch (Exception e) {
                System.err.println("Error checking psql or not installed.");
            }
        }

        // Final judgment
        if (isPostgresServiceRunning || isPostgresInstalled) {
            System.out.println("✅ PostgreSQL is installed on the system.");
        } else {
            System.out.println("❌ PostgreSQL is not installed or not running.");
        }

        return isPostgresInstalled;
    }

    public void installPostgresWindows() {
        String downloadUrl = "https://get.enterprisedb.com/postgresql/postgresql-17.5-3-windows-x64.exe";
        try {
            // 1. Create temporary file path
            File tempInstaller = File.createTempFile("postgresql_installer_", ".exe");

            // 2. Download
            System.out.println("Downloading PostgreSQL installer from: " + downloadUrl);
            try (BufferedInputStream in = new BufferedInputStream(new java.net.URL(downloadUrl).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(tempInstaller)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
            System.out.println("Download complete: " + tempInstaller.getAbsolutePath());

            // 3. Run unattended installation
            ProcessBuilder builder = new ProcessBuilder(
                    tempInstaller.getAbsolutePath(),
                    "--mode", "unattended",
                    "--superpassword", "postgres"
            );
            builder.inheritIO();
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("PostgreSQL installation completed successfully.");
            } else {
                System.err.println("PostgreSQL installer exited with code: " + exitCode);
            }

            // 4. Delete installer file
            if (tempInstaller.delete()) {
                System.out.println("Temporary installer file deleted.");
            } else {
                System.out.println("Failed to delete temporary installer file: " + tempInstaller.getAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("Failed to download or install PostgreSQL:");
            e.printStackTrace();
        }
    }
}
