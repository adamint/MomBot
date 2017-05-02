package utils;

public class MathUtils {
    public static boolean isNumber(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }
}
