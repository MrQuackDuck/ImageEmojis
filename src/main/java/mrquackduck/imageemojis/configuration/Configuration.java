package mrquackduck.imageemojis.configuration;

import mrquackduck.imageemojis.enums.EnforcementPolicy;
import mrquackduck.imageemojis.enums.SuggestionMode;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration extends MessageConfigurationBase {
    public Configuration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    public String serverIp() {
        return getString("serverIp");
    }

    public int webServerPort() {
        return getInt("webServerPort");
    }

    public EnforcementPolicy enforcementPolicy() {
        return getEnumValue("enforcementPolicy", EnforcementPolicy.class, EnforcementPolicy.OPTIONAL);
    }

    public boolean isAnvilReplacementEnabled() {
        return getBoolean("replaceInAnvil");
    }

    public boolean isSignReplacementEnabled() {
        return getBoolean("replaceInSigns");
    }

    public boolean isCommandReplacementEnabled() {
        return getBoolean("replaceInCommands");
    }

    public boolean isEmojiHoverEnabled() {
        return getBoolean("emojiHoverEnabled");
    }

    public String emojiHoverColor() {
        return getString("emojiHoverColor");
    }

    public String templateFormat() {
        return getString("templateFormat");
    }

    public SuggestionMode suggestionMode() {
        return getEnumValue("suggestionMode", SuggestionMode.class, SuggestionMode.TEMPLATES);
    }

    public boolean isMergeWithServerResourcePackEnabled() {
        return getBoolean("mergeWithServerResourcePack");
    }

    public String mergeServerResourcePackName() {
        return getString("mergeServerResourcePackName");
    }

    public boolean isExtendedUnicodeRangeEnabled() {
        return getBoolean("extendedUnicodeRange");
    }
}
