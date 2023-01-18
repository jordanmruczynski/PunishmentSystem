package pl.jordii.punishmentsystemproxy.utils;

import java.time.Duration;

public class TimeParser {

    private TimeParser() { //disable auto constructor creator
        throw new AssertionError();
    }

    public static Duration parseTime(String time) {
        String[] parts = time.split(" ");
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long months = 0;
        long years = 0;
        for (String part : parts) {
            char unit = part.charAt(part.length()-1);
            int value = Integer.parseInt(part.substring(0, part.length()-1));
            switch (unit) {
                case 's':
                    seconds += value;
                    break;
                case 'm':
                    minutes += value;
                    break;
                case 'h':
                    hours += value;
                    break;
                case 'd':
                    days += value;
                    break;
                case 'M':
                    months += value;
                    break;
                case 'y':
                    years += value;
                    break;
            }
        }
        return Duration.ofSeconds(seconds + minutes * 60 + hours * 3600 + days * 86400 + months * 2592000 + years * 31536000);
    }
}
