package mrquackduck.imageemojis.utils;

import java.util.regex.Pattern;

public class VersionComparer {
    public static boolean isVersionHigherOrEqualThan(String version, String versionToCompareWith) {
        String cleanVersion = cleanVersion(version);
        String cleanVersionToCompare = cleanVersion(versionToCompareWith);

        return compareVersions(cleanVersion, cleanVersionToCompare) >= 0;
    }

    public static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }

        return 0;
    }

    private static String cleanVersion(String version) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+(?:\\.\\d+)?)");
        java.util.regex.Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return version;
    }
}