package com.shohhet.servletapp.utils;

public class ServletUtils {

    public static boolean isInteger(String maybeInteger) {
        try {
            Integer.parseInt(maybeInteger);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
