package mrquackduck.imageemojis.setup;

import com.tchristofferson.configupdater.ConfigUpdater;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.services.implementations.EmojiRepository;
import mrquackduck.imageemojis.services.implementations.EmojiResourcePackGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PluginInitializer {
    private final ImageEmojisPlugin plugin;

    public PluginInitializer(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.saveDefaultConfig();

        // Update the config with missing key-pairs (and remove redundant ones if present)
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try { ConfigUpdater.update(plugin, "config.yml", configFile, new ArrayList<>()); }
        catch (IOException e) { e.printStackTrace(); }

        createEmojisFolderIfNotExists();

        plugin.reloadConfig();
        saveDefaultPackPng();

        plugin.setEmojiRepository(new EmojiRepository(plugin));
        EmojiResourcePackGenerator generator = new EmojiResourcePackGenerator(plugin);
        plugin.setResourcePack(generator.generate());
    }

    private void createEmojisFolderIfNotExists() {
        File emojiFolder = new File(plugin.getDataFolder(), "emojis");
        if (!emojiFolder.exists()) {
            if (emojiFolder.mkdirs()) plugin.getLogger().info("Created emojis folder.");
            else plugin.getLogger().warning("Failed to create emojis folder.");
        }
    }

    private void saveDefaultPackPng() {
        File pack = new File(plugin.getDataFolder(), "pack.png");
        if (!pack.exists()) {
            plugin.saveResource("pack.png", false);
        }
    }
}
