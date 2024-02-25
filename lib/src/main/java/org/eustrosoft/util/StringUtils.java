package org.eustrosoft.util;

public final class StringUtils {
    public static final String EMPTY_STRING = "";

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return isEmpty(str.replaceAll("\\s+", ""));
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    private StringUtils() {

    }
}
