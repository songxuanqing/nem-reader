
---

# How to Run NemReader

---

## 1. Prerequisites

* PostgreSQL is recommended to be installed and running on your system.
* If PostgreSQL is not found on Windows, the program will detect this and attempt to download and install it automatically.

---

## 2. Build the Project

Build the executable JAR with all dependencies using Maven:

```bash
mvn clean package
```

This will generate a shaded JAR file including all dependencies, usually located at:
`target/NemReader-1.0-SNAPSHOT.jar`

---

## 3. Run the Program

### 3.1 Run Without Docker

Make sure you have Java 21 installed, then run:

```bash
java -jar target/NemReader-1.0-SNAPSHOT.jar
```

아래처럼 문장을 추가하여, PostgreSQL이 최초 설치되어 사용자(DB 사용자 및 DB)가 없는 경우 슈퍼유저 정보를 받아 생성하는 절차까지 포함해 자연스럽게 설명했습니다:

**Program Behavior:**

* The program first checks if Docker is running:

   * If Docker is running, it connects directly to the PostgreSQL instance inside the Docker container.
   * If Docker is not running, it checks whether PostgreSQL is installed locally.

      * If PostgreSQL is not installed, on Windows it will automatically download and install PostgreSQL silently.
      * If PostgreSQL is installed but required database user or database does not exist yet, the program will prompt for PostgreSQL superuser credentials to create the necessary user and database.
      * If PostgreSQL is installed and users/databases exist, it proceeds normally.
* Flyway migrations run automatically to prepare and update the database schema.
* The program then prompts you to enter the path to the NEM12 file to process.

   * You must enter a valid file path.
   * If the path is invalid or processing fails, you will be prompted repeatedly until a valid file is processed successfully.
* Upon successful processing of the NEM12 file, the program confirms success and then exits.



---

### Important Note About Windows Paths and Docker

Windows file paths use backslashes (`\`) and drive letters with colons (e.g., `C:\Users\YourName\Documents`).
When using Docker, **you must convert Windows paths to Docker-friendly format using forward slashes (`/`)** and replace the colon (`:`) after the drive letter with a slash.

For example:

```
C:\Users\YourName\Documents
```

should be written as

```
C:/Users/YourName/Documents
```

in Docker commands and volume mounts.

---

### 3.2 Run With Docker

**Example:**

If your NEM12 file is located at:

```
C:\Users\YourUser\Documents\example.csv
```

Run the container by mounting the folder containing your file (note the use of forward slashes `/`):

```bash
docker-compose run --rm -v C:/Users/YourUser/Documents:/mnt/nem-reader app
```

When the program prompts you to enter the path to the NEM12 file, enter the **path inside the container**:

```
Enter path to NEM12 file: /mnt/nem-reader/example.csv
```

---

### Docker Compose Volume Mount Example

```yaml
services:
  app:
    volumes:
      - C:/Users/YourUser/Documents:/mnt/nem-reader
```

---

This setup ensures the container has access to your local files and that you can input the correct path when prompted.

---



### ⚡ Performance

The application was tested with **43,223 unique date entries** and a total of **2,074,704 consumption records** (at 30-minute intervals).
On the following system, **all transactions completed and were successfully stored in under 2 minutes**:

* **OS**: Windows 11 Home (Build 26100)
* **CPU**: AMD Ryzen (2.1GHz, 4 cores)
* **RAM**: 10 GB
* **Storage**: SSD
* **JVM**: Java 21
* **Database**: PostgreSQL 17.5-3 (windows x64)
* **Data Set**: src/resources/sample/sample-nem12.csv

This confirms the application's ability to handle large-scale NEM12 data files efficiently on a typical consumer-grade machine.

---


# Answering the following questions:


### ✅ Q1. What is the rationale for the technologies you have decided to use?

1. **Java (Core Language):**
   Java offers a strong type system, mature multithreading, and rich standard libraries (e.g., `java.nio`, `java.time`, `java.sql`) which make it ideal for building a robust, file-based data ingestion system. Its object-oriented model also facilitates encapsulating the semantics of NEM12 data records (types 200, 300, 400, 500) into maintainable classes.

2. **PostgreSQL (Database):**
   PostgreSQL was chosen for its:

    * ACID compliance and stability,
    * Strong support for time-series and structured tabular data,
    * Compatibility with JDBC and Flyway,
    * Ability to scale for large insert workloads using batch insert.

3. **JDBC with Batch Insertion:**
   Instead of individual inserts, JDBC batch processing is used to:

    * Reduce round-trips to the database,
    * Bundle network I/O and commit costs,
    * Enable batch-level retries in case of failures.

4. **BufferedReader-based Streaming:**
   To minimize memory usage and support large files, the entire file is not loaded into memory at once. Line-by-line streaming prevents `OutOfMemoryError` when processing large (100MB\~1GB) NEM12 datasets.

5. **Modular Parser Design (Nem12ParserState):**
   A state-machine based parser maintains context between rows (200 → 300 → 400 → 500), which is crucial to correctly associate interval data with the corresponding NMI and register. This design also enables extensibility and easier testing.

6. **Flyway (Database Migration Tool):**
   Flyway automates and version-controls database schema changes, allowing:

    * Zero-touch DB setup on first run (even if the database or user doesn’t yet exist),
    * Repeatable and traceable migrations,
    * Environment-independent database deployment.

7. **Dockerization to Support Environments Without Local PostgreSQL:**
   To improve portability, automate environment setup, and enhance user convenience, the entire application—including PostgreSQL—is containerized using Docker and Docker Compose. This approach ensures the application can run seamlessly in any environment, regardless of whether PostgreSQL is installed locally, by automatically launching a PostgreSQL container alongside the Java app container. It also simplifies the setup process and enables automated database migrations on startup, reducing manual configuration and setup effort for users and developers alike.


✅ **Q2. What would you have done differently if you had more time?**

1. **NMI Register-Level Aggregation and Storage:**
   Currently, data is flattened per NMI without register-level granularity. With more time, I would have normalized the schema to store each `nmi + suffix` combination separately, allowing support for multi-energy households (e.g., solar, battery, grid).

2. **Quality Event (400) Parsing for V Quality Flags:**
   Records with quality method `V` require dynamic interpretation based on subsequent 400 records. I would refactor the parser to:

   * Detect and buffer `V`-flagged 300s,
   * Correlate them with associated 400s,
   * Store granular quality metadata per interval.

3. **B2B Data (500) Integration:**
   Manual meter reads from 500 records were parsed but not persisted. I would introduce logic to store this data separately, with a possible fallback mechanism to use 500 data in case of missing 300 readings.

4. **Client UI and Energy Analysis Tools:**
   Given additional time, I would have developed a lightweight client interface or integrated data visualization tools (e.g., using Python, R, or web-based dashboards) to analyze energy consumption patterns, detect anomalies, or generate reports from the stored data—making the solution more useful for end-users and analysts.

5. **Centralized Logging (e.g., SLF4J + Logback):**
   The current implementation uses `System.out` and `System.err`. I would introduce a logging facade (e.g., SLF4J) to enable:

   * Log level control (INFO, DEBUG, ERROR),
   * File-based or remote logging (e.g., ELK stack),
   * Better traceability in production environments.

6. **Parallelization / Asynchronous Processing:**
   The current line-by-line model is sequential. I would explore stream partitioning and thread-safe batch queues to enable parallel or reactive processing—especially beneficial for multi-core ingestion environments.


✅ Q3. What is the rationale for the design choices that you have made?

1. API Design

* The rationale behind my design choices is based on the principles of separation of concerns, scalability, and clean architecture layering.

* At the User Infra layer, I receive terminal input from the user, keeping this interface logic isolated from business logic.

* In the Service layer, core business processing such as file batch handling is encapsulated within a utility class (Nem12Reader). This class coordinates parsing and saving while remaining decoupled from input/output concerns.

* The Entity (model) layer is split into two packages:
  model.csv for representing parsed structures (e.g., Nem12IntervalData300, Nem12Header100),
  and model.db for database-mapped entities (e.g., MeterReading).
  This separation ensures that parsing logic and persistence concerns don’t overlap.

* A state-driven parsing design using Nem12ParserState enables scalable, context-aware parsing of complex NEM12 formats.

* The Persistence layer is handled via the repository package (e.g., MeterReadingRepository), which encapsulates all database interactions, following the repository pattern to support maintainability and testability.

* Configuration and database migration logic are placed in separate folders (config, resources/db.migration), supporting modular and environment-agnostic design.

* Overall, this structure follows layered architecture principles to ensure that each component has a single responsibility. It promotes modularity, maintainability, and future extensibility, such as supporting new NEM formats, scaling to larger datasets, or exposing a REST API in the future.

2. Stream-Oriented Processing with Stateful Parser:
   We adopted a stateful streaming architecture to handle context-sensitive formats like NEM12. This allows resuming from partial progress, supports fault-tolerant streaming, and sets the foundation for retry mechanisms.

3. Batching Logic (1000 rows):
   We chose to batch every 1000 readings before inserting into the database. This size is a balance:

* Small enough to prevent memory bloat,

* Large enough to reduce DB round-trips,

* Compatible with JDBC driver’s batch size limits.

4. Exception Handling and Resource Safety:

* We used try-with-resources for all I/O and DB connections to prevent resource leaks.

* All major failure points (e.g., DB insert, file parsing) are guarded and logged, improving system reliability.

5. Separation of Concerns:

* Postgresql.java handles service checks and connection pooling.

* FlywayMigrationRunner.java handles environment preparation.

* FileReadingBatch.java encapsulates ingestion logic.
This modularity improves testability, maintainability, and simplifies future enhancements.

6. Interactive CLI with Validation:
   The main() method prompts the user for input and validates the file path before execution. It improves UX and prevents early runtime failures.


