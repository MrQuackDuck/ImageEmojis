package mrquackduck.imageemojis.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class ConfigurationBase {
    private final JavaPlugin plugin;

    public ConfigurationBase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected String getString(String path) {
        return getConfig().getString(path);
    }

    protected int getInt(String path) {
        return getConfig().getInt(path);
    }

    protected boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    protected double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    protected long getLong(String path) {
        return getConfig().getLong(path);
    }

    protected List<?> getList(String path) {
        return getConfig().getList(path);
    }

    protected <T extends Enum<T>> T getEnumValue(String path, Class<T> enumClass, T defaultValue) {
        String value = getString(path);
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Invalid enum value '" + value + "' at path: '"+ path + "'. Using default: '" + defaultValue + "'");
            return defaultValue;
        }
    }

    private FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }
}
