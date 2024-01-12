package main.java.elementalmp4.utils;

import java.util.Optional;

public class Converter {

    public static Optional<Integer> tryStringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
