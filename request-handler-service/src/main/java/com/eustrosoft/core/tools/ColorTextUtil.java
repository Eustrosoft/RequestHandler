package com.eustrosoft.core.tools;

public final class ColorTextUtil {

    private ColorTextUtil() {

    }

    public static String getColoredString(String text, Color color) {
        return color.getColoredTest(text);
    }

    public enum Color {
        RED("\u001B[31m", "\u001B[0m"),
        GREEN("\u001B[32m", "\u001B[0m"),
        BLUE("\u001B[34m", "\u001B[0m");

        final String prefix;
        final String postfix;

        Color(String prefix, String postfix) {
            this.prefix = prefix;
            this.postfix = postfix;
        }

        public String getColoredTest(String text) {
            return String.format("%s%s%s", this.prefix, text, this.postfix);
        }
    }
}
