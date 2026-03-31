package com.ecowaste.smartbilling.model;

public enum WasteCategory {
    RECYCLABLE,
    REUSABLE,
    ECO_DISPOSAL;

    public static WasteCategory fromDatabaseValue(String value) {
        if (value == null) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "RECYCLABLE", "RECYCLED" -> RECYCLABLE;
            case "REUSABLE", "REUSE", "REUSED" -> REUSABLE;
            case "ECO_DISPOSAL", "ECODISPOSAL", "ECO-DISPOSAL" -> ECO_DISPOSAL;
            default -> throw new IllegalArgumentException("Unknown waste category: " + value);
        };
    }
}
