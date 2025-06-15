package mrquackduck.imageemojis.configuration;

import mrquackduck.imageemojis.utils.MessageColorizer;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageConfigurationBase extends ConfigurationBase {
    private final String messagesConfigurationSectionName;

    public MessageConfigurationBase(JavaPlugin plugin, String messagesConfigurationSectionName) {
        super(plugin);
        this.messagesConfigurationSectionName = messagesConfigurationSectionName;
    }

    /**
     * Returns a plain message from configuration by key without formatting
     */
    public String getPlainMessage(String key) {
        var message = getString(messagesConfigurationSectionName + '.' + key);
        if (message == null) return String.format("Message %s wasn't found", key);

        return message;
    }

    /**
     * Returns a formatted message from configuration by key
     */
    public String getMessage(String key) {
        return MessageColorizer.colorize(getPlainMessage(key));
    }
}
