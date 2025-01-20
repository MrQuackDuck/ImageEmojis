package mrquackduck.imageemojis.utils;

public class ColorUtil {
    public static int hexToColor(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) throw new IllegalArgumentException("Hex color string cannot be null or empty");
        if (hexColor.startsWith("#")) hexColor = hexColor.substring(1);

        // Ensure the remaining string is a valid hex color
        if (!hexColor.matches("^[0-9a-fA-F]{6}$")) throw new IllegalArgumentException("Invalid hex color string: " + hexColor);

        // Convert the hex string to an integer
        return Integer.parseInt(hexColor, 16);
    }
}
