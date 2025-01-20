package mrquackduck.imageemojis.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageColorizer {
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    // Colorize the raw message
    public static String colorize(String str)
    {
        Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', str));
        StringBuilder buffer = new StringBuilder();

        while (matcher.find())
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());

        return matcher.appendTail(buffer).toString();
    }
}