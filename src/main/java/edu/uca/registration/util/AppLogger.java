package edu.uca.registration.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final boolean DEBUG = Boolean.getBoolean("debug.enabled");

    public static void info(String message) {
        if (DEBUG) {
            log("INFO", message);
        }
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    private static void log(String level, String message) {
        System.err.println("[" + LocalDateTime.now().format(fmt) + "] [" + level + "] " + message);
    }
}