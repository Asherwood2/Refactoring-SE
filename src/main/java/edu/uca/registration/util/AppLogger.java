package edu.uca.registration.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean debugEnabled;

    public AppLogger(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void info(String message) {
        if (debugEnabled) {
            log("INFO", message);
        }
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    private void log(String level, String message) {
        System.err.println("[" + LocalDateTime.now().format(fmt) + "] [" + level + "] " + message);
    }
}