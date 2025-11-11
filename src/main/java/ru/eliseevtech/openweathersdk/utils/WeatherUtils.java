package ru.eliseevtech.openweathersdk.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WeatherUtils {

    public static String nvl(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }

    public static String required(String s, String msg) {
        if (s == null || s.isBlank()) {
            throw new IllegalStateException(msg);
        }
        return s;
    }

}
