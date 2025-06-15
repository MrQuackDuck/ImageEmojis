package mrquackduck.imageemojis.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CharUtil {
    public static String parseLongToUtf8Code(long number) {
        return String.format("\\u%04X", number);
    }

    public static String parseUtf8CodeToActualSymbol(String utf8Code) {
        StringBuilder output = new StringBuilder();
        int length = utf8Code.length();

        for (int i = 0; i < length; i++) {
            if (i < length - 5 && utf8Code.charAt(i) == '\\' && utf8Code.charAt(i + 1) == 'u') {
                String hexCode = utf8Code.substring(i + 2, i + 6);
                try {
                    int codePoint = Integer.parseInt(hexCode, 16);
                    output.append((char) codePoint);
                    i += 5; // Skip the Unicode escape sequence
                } catch (NumberFormatException e) {
                    // If parsing fails, treat it as a regular character
                    output.append(utf8Code.charAt(i));
                }
            } else {
                output.append(utf8Code.charAt(i));
            }
        }

        return output.toString();
    }

    public static long parseUtf8CodeToLong(String utf8Code) {
        if (utf8Code.startsWith("\\u")) {
            String hexValue = utf8Code.substring(2);
            return Long.parseLong(hexValue, 16);
        }
        else throw new IllegalArgumentException("Invalid UTF-8 code format");
    }

    public static long hashToRange(String input, long start, long end) {
        if (start >= end) throw new IllegalArgumentException("Start of range must be less than end of range.");

        // Create SHA-256 hash
        MessageDigest digest;
        try { digest = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        byte[] hashBytes = digest.digest(input.getBytes());

        // Convert hash bytes to a positive number
        BigInteger hashValue = new BigInteger(1, hashBytes);

        // Calculate the range size
        long rangeSize = end - start;

        // Map the hash value to the desired range and return
        return start + (hashValue.mod(BigInteger.valueOf(rangeSize)).longValue());
    }

    public static String generateSHA256(String input) {
        MessageDigest digest;
        try { digest = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        byte[] hashBytes = digest.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
