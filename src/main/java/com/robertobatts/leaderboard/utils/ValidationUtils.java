package com.robertobatts.leaderboard.utils;

import com.robertobatts.leaderboard.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static String checkIsNotBlank(String string, String exceptionMessage) {
        if (StringUtils.isBlank(string)) {
            throw new ValidationException(exceptionMessage);
        }
        return string;
    }

    public static long checkIsGte(Long value, long min, String exceptionMessage) {
        if (value == null || value < min) {
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
