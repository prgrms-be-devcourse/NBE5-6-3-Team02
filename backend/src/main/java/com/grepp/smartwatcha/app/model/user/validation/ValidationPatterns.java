package com.grepp.smartwatcha.app.model.user.validation;

public class ValidationPatterns {
    public static final String PHONE_NUMBER_PATTERN = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

    private ValidationPatterns() {
        // Utility class
    }
} 