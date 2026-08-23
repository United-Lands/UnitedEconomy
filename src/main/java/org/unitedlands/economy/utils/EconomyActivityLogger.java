package org.unitedlands.economy.utils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EconomyActivityLogger {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ENTRY_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Path logDirectory;

    public static void init(Path directory) {
        logDirectory = directory;
        try {
            Files.createDirectories(logDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create log directory: " + directory, e);
        }
    }

    public static void log(String message) {
        writeEntry("INFO", message);
    }

    public static void warn(String message) {
        writeEntry("WARN", message);
    }

    public static void error(String message, Throwable throwable) {
        if (throwable != null) {
            writeEntry("ERROR", message + " | " + throwable);
            for (StackTraceElement el : throwable.getStackTrace()) {
                writeEntry("ERROR", "    at " + el);
            }
        } else {
            writeEntry("ERROR", message);
        }
    }

    public static void error(String message) {
        error(message, null);
    }

    private static void writeEntry(String level, String message) {
        if (logDirectory == null) {
            throw new IllegalStateException("DailyLogger has not been initialised. Call DailyLogger.init() in onEnable().");
        }

        String timestamp = LocalDateTime.now().format(ENTRY_TIMESTAMP_FORMAT);
        String line = "[" + timestamp + "] [" + level + "] " + message + System.lineSeparator();

        Path logFile = logDirectory.resolve(LocalDate.now().format(FILE_DATE_FORMAT) + ".log");

        try {
            Files.writeString(logFile, line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("[DailyLogger] Failed to write to log file: " + e.getMessage());
            System.err.print(line);
        }
    }
}