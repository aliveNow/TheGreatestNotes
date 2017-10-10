package ru.altarix.thegreatestnotes.utils;

/**
 * Created by samsmariya on 06.10.17.
 */

public class Utils {
    // Objects.requireNonNull начинается с 19 API, увы
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }

    public static void illegalArgument(String message) {
        throw new IllegalArgumentException(message);
    }
}
