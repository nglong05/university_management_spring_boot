package com.example.university.entity;

public enum Gender {
    M, F, O;

    public static Gender fromDb(String dbValue) {
        // fallback
        if (dbValue == null) return null;
        switch (dbValue.toUpperCase()) {
            case "M": return M;
            case "F": return F;
            case "O": return O;
            default:  return null;
        }
    }

    public String toDb() {
        return name();
    }
}
