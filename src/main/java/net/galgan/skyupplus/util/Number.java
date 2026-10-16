package net.galgan.skyupplus.util;

import java.util.Locale;

public class Number {

    public static String format(int number) {
        if (number > -1000 && number < 1000) {
            return String.valueOf(number);
        }

        if (number > -1000000 && number < 1000000) {
            double truncated = Math.floor((double) number / 100) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "k";
        }

        if (number > -1000000000 && number < 1000000000) {
            double truncated = Math.floor((double) number / 100000) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "M";
        }

        double truncated = Math.floor((double) number / 100000000) / 10;
        return String.format(Locale.US, "%.1f", truncated) + "B";
    }

    public static String format(double number) {
        if (number > -1000 && number < 1000) {
            if (number == Math.floor(number)) {
                return String.valueOf((long) number);
            }
            return String.format(Locale.US, "%.1f", number);
        }

        if (number > -1000000 && number < 1000000) {
            double truncated = Math.floor(number / 100) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "k";
        }

        if (number > -1000000000 && number < 1000000000) {
            double truncated = Math.floor(number / 100000) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "M";
        }

        double truncated = Math.floor(number / 100000000) / 10;
        return String.format(Locale.US, "%.1f", truncated) + "B";
    }

    public static String format(long number) {
        if (number > -1000 && number < 1000) {
            return String.valueOf(number);
        }

        if (number > -1000000 && number < 1000000) {
            double truncated = Math.floor((double) number / 100) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "k";
        }

        if (number > -1000000000 && number < 1000000000) {
            double truncated = Math.floor((double) number / 100000) / 10;
            return String.format(Locale.US, "%.1f", truncated) + "M";
        }

        double truncated = Math.floor((double) number / 100000000) / 10;
        return String.format(Locale.US, "%.1f", truncated) + "B";
    }

    public static String formatTime(int number) {
        boolean negative = number < 0;
        number = Math.abs(number);

        int hours = number / 3600;
        int minutes = (number % 3600) / 60;
        int seconds = number % 60;

        String result;
        if (hours > 0) {
            result = String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            result = String.format("%dm %ds", minutes, seconds);
        } else {
            result = String.format("%ds", seconds);
        }

        return negative ? "-" + result : result;
    }

    public static String formatTimeNoSeconds(int number) {
        boolean negative = number < 0;
        number = Math.abs(number);

        int hours = number / 3600;
        int minutes = (number % 3600) / 60;

        String result;
        if (hours > 0) {
            result = String.format("%dh %dm", hours, minutes);
        } else {
            result = String.format("%dm", minutes);
        }

        return negative ? "-" + result : result;
    }
}
