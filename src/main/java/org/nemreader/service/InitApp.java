package org.nemreader.service;

import org.nemreader.util.DockerUtil;
import org.nemreader.util.FileReadingBatch;
import org.nemreader.util.FlywayMigrationRunner;
import org.nemreader.util.Postgresql;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InitApp {

    public void init(Scanner scanner) {
        DataSource dataSource;
        Postgresql pg = new Postgresql();

        // 1. Check Docker installation
        boolean isDockerRunning = DockerUtil.isDockerRunning();

        if (isDockerRunning) {
            System.out.println("Docker is running. Connecting to PostgreSQL inside Docker container.");
        } else {
            System.out.println("Docker is not installed or not running.");
            // 2. Check if local PostgreSQL is installed
            if (!pg.isPostgresInstalled()) {
                System.out.println("PostgreSQL not detected, starting installation...");
                pg.installPostgresWindows();
            } else {
                System.out.println("PostgreSQL is already installed.");
            }
        }

        new FlywayMigrationRunner(isDockerRunning).run();
        dataSource = pg.getDataSource(isDockerRunning);
        // 3. Input and process NEM12 file path
        System.out.print("Enter path to NEM12 file: ");
        while (true) {
            try {
                String filePathStr = scanner.nextLine().trim();
                Path inputPath = Paths.get(filePathStr);

                boolean success = new FileReadingBatch().run(inputPath, dataSource);
                if (success) {
                    System.out.println("File processed successfully.");
                    break;
                } else {
                    System.err.println("File processing failed. Please try again.");
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Invalid file path: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error during file processing:");
                e.printStackTrace();
                System.err.println("Please try again.");
            }

            System.out.print("Enter path to NEM12 file: ");
        }
    }
}
