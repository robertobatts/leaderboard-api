package com.robertobatts.leaderboard.utils;

import com.robertobatts.leaderboard.exception.ValidationException;

public class ValidationUtils {

    public static String checkIsNotNullOrEmpty(String string, String exceptionMessage) {
        if (string == null || string.isEmpty()) {
            throw new ValidationException(exceptionMessage);
        }
        return string;
    }

    public static long checkIsGte(long value, long min, String exceptionMessage) {
        if (value < min) {
            throw new ValidationException(exceptionMessage);
        }
        return value;
    }

    public static <T> T isNotNull(T t, String exceptionMessage) {
        if (t == null) {
            throw new ValidationException(exceptionMessage);
        }
        return t;
    }
}
