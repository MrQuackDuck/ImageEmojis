package mrquackduck.imageemojis.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class PackFormatUtil {
    public static int getPackFormat(String version) {
        Map<String, Integer> versionToPackFormat = Map.ofEntries(
                entry("1.13–1.14.4", 4),
                entry("1.15–1.16.1", 5),
                entry("1.16.2–1.16.5", 6),
                entry("1.17–1.17.1", 7),
                entry("1.18–1.18.1", 8),
                entry("1.18.2", 9),
                entry("1.19–1.19.3", 10),
                entry("1.19.4", 12),
                entry("1.20–1.20.1", 15),
                entry("1.20.2", 18),
                entry("1.20.3–1.20.4", 26),
                entry("1.20.5–1.20.6", 41),
                entry("1.21–1.21.1", 48),
                entry("1.21.2–1.21.3", 57),
                entry("1.21.4", 61)
        );

        String cleanVersion = cleanVersion(version);

        // Compare the current version with the ranges in the map
        for (Map.Entry<String, Integer> entry : versionToPackFormat.entrySet()) {
            String versionRange = entry.getKey();
            if (isVersionInRange(cleanVersion, versionRange)) {
                return entry.getValue();
            }
        }

        // If no match is found, return the latest pack format
        return versionToPackFormat.values().stream().max(Integer::compare).orElse(0);
    }

    private static String cleanVersion(String version) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return version; // Return original if no match found
    }

    private static boolean isVersionInRange(String currentVersion, String versionRange) {
        String[] parts = versionRange.split("–");
        String minVersion = parts[0];
        String maxVersion = parts.length > 1 ? parts[1] : minVersion;

        return VersionComparer.compareVersions(currentVersion, minVersion) >= 0 &&
                VersionComparer.compareVersions(currentVersion, maxVersion) <= 0;
    }
}