package mrquackduck.imageemojis.configuration;

import mrquackduck.imageemojis.types.enums.EnforcementPolicy;
import mrquackduck.imageemojis.types.enums.NoPermAction;
import mrquackduck.imageemojis.types.enums.SuggestionMode;
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
        return getBoolean("replaceInAnvils");
    }

    public boolean isSignReplacementEnabled() {
        return getBoolean("replaceOnSigns");
    }

    public boolean isCommandReplacementEnabled() {
        return getBoolean("replaceInCommands");
    }

    public NoPermAction inChatNoPermAction() {
        return getEnumValue("noPermAction.inChat", NoPermAction.class, NoPermAction.ERASE_EMOJIS);
    }

    public NoPermAction inAnvilsNoPermAction() {
        return getEnumValue("noPermAction.inAnvils", NoPermAction.class, NoPermAction.ERASE_EMOJIS);
    }

    public NoPermAction onSignsNoPermAction() {
        return getEnumValue("noPermAction.onSigns", NoPermAction.class, NoPermAction.ERASE_EMOJIS);
    }

    public NoPermAction inCommandsNoPermAction() {
        return getEnumValue("noPermAction.inCommands", NoPermAction.class, NoPermAction.ERASE_EMOJIS);
    }

    public boolean shouldNoPermMessageAppear() {
        return getBoolean("noPermMessage");
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
