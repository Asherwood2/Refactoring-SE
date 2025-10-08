package edu.uca.registration.util;

import edu.uca.registration.exception.ValidationException;

public class Validator {

    public static void validateBannerId(String id) {
        if (id == null || id.trim().isEmpty() || !id.matches("B\\d{3,}")) {
            throw new ValidationException("Invalid Banner ID (must be B followed by 3+ digits)");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.contains("@") || email.indexOf("@") == email.length() - 1) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validateNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateCapacity(int capacity) {
        if (capacity < 1 || capacity > 500) {
            throw new ValidationException("Capacity must be between 1 and 500");
        }
    }
}