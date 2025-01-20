package mrquackduck.imageemojis;

import mrquackduck.imageemojis.commands.EmojisCommand;
import mrquackduck.imageemojis.listeners.*;
import mrquackduck.imageemojis.models.ResourcePack;
import mrquackduck.imageemojis.services.EmojiRepository;
import mrquackduck.imageemojis.services.EmojiResourcePackGenerator;
import mrquackduck.imageemojis.services.HttpResourcePackServer;
import mrquackduck.imageemojis.utils.MessageColorizer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mrquackduck.imageemojis.utils.EnvironmentUtil.isPaper;
import static mrquackduck.imageemojis.utils.EnvironmentUtil.isSpigot;
import static mrquackduck.imageemojis.utils.VersionComparer.isVersionHigherOrEqualThan;

public final class ImageEmojisPlugin extends JavaPlugin {
    private String resourcePackDownloadUrl = "";
    private EmojiRepository emojiRepository;
    private ResourcePack resourcePack;
    private HttpResourcePackServer resourcePackServer;
    private Logger logger;
    private static Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        // Setting up a logger
        logger = getLogger();

        // Preventing the execution if the server runs on Spigot
        if (isSpigot() && !isPaper()) {
            getServer().getConsoleSender().sendMessage(MessageColorizer.colorize("&c[ImageEmojis] Spigot detected! The plugin doesn't support Spigot yet due to the lack of compatibility with many features of Paper API." +
                    "&r\n&c[!] Please use Paper 1.19.4 and above (&nhttps://papermc.io/downloads/all&c)"));
            return;
        }

        // Preventing the execution if the server runs below 1.19.4
        if (!isVersionHigherOrEqualThan(getServer().getBukkitVersion(), "1.19.4")) {
            getServer().getConsoleSender().sendMessage(MessageColorizer.colorize("&c[ImageEmojis] &nOld version detected!&c The plugin is not supported on that version." +
                    "&r\n&c[!] Please use Paper 1.19.4 and above (&nhttps://papermc.io/downloads/all&c)"));
            return;
        }

        // Creating the "emojis" folder if it didn't exist yet
        createEmojisFolder();

        // Setting up commands
        Objects.requireNonNull(getCommand("emojis")).setExecutor(new EmojisCommand(this));

        // Setting up listeners
        getServer().getPluginManager().registerEvents(new SendMessageListener(this), this);
        getServer().getPluginManager().registerEvents(new SendCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilRenameListener(this), this);
        getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);

        // Starting the plugin
        try { start(); }
        catch (RuntimeException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            return;
        }

        // Start HTTP server to serve the resource pack
        startHttpServer(resourcePack.getPath());
    }

    @Override
    public void onDisable() {
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            logger.info("Resource pack server stopped");
        }
    }

    public EmojiRepository getEmojiRepository() {
        return emojiRepository;
    }

    public String getResourcePackDownloadUrl() {
        return resourcePackDownloadUrl;
    }

    public ResourcePack getResourcePack() {
        return resourcePack;
    }

    private void start() {
        // Save default configuration
        saveDefaultConfig();

        // Saving the default pack icon if it doesn't exist
        saveDefaultPackPng();

        // Setting up messages
        setupMessages();

        // Setting up the emoji repository and getting the emoji data from the "./emojis/" directory
        emojiRepository = new EmojiRepository(this);

        // Generating the resource pack
        EmojiResourcePackGenerator generator = new EmojiResourcePackGenerator(this);
        resourcePack = generator.generate();
    }

    public void reload() {
        reloadConfig();
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            logger.info("Resource pack server restarting...");
        }

        start();

        // Start HTTP server to serve the resource pack
        startHttpServer(resourcePack.getPath());

        logger.info("Plugin restarted!");
    }

    private void saveDefaultPackPng() {
        File pack = new File(getDataFolder() + File.separator + "pack.png");
        if (pack.exists()) return;
        saveResource("pack.png", false);
    }

    private void startHttpServer(String resourcePackPath) {
        FileConfiguration config = getConfig();

        String urlBase = config.getString("serverIp");
        int port = config.getInt("webServerPort");

        try {
            File resourcePackFile = new File(resourcePackPath);
            resourcePackServer = new HttpResourcePackServer(port, resourcePackFile);
            resourcePackServer.start();
            resourcePackDownloadUrl = "http://" + urlBase + ":" + port + "#" + resourcePack.getHashString();
            logger.info("Resource pack server started at " + resourcePackDownloadUrl);
        } catch (IOException e) {
            logger.severe("Failed to start HTTP server: " + e.getMessage());
        }
    }

    private void createEmojisFolder() {
        File emojiFolder = new File(getDataFolder(), "emojis");
        if (!emojiFolder.exists()) {
            if (emojiFolder.mkdirs()) getLogger().info("Created emojis folder.");
            else getLogger().warning("Failed to create emojis folder.");
        }
    }

    private void setupMessages() {
        messages = new HashMap<>();

        // Getting the messages from the config
        ConfigurationSection configSection = getConfig().getConfigurationSection("messages");
        if (configSection != null) {
            // Adding these messages to dictionary
            Map<String, Object> messages = configSection.getValues(true);
            for (Map.Entry<String, Object> pair : messages.entrySet()) {
                ImageEmojisPlugin.messages.put(pair.getKey(), pair.getValue().toString());
            }
        }

        saveDefaultConfig();
    }

    // Returns a message from the config by key
    public static String getMessage(String key) {
        if (messages == null) return String.format("Message %s wasn't found (messages list is null)", key);
        if (messages.get(key) == null) return String.format("Message %s wasn't found", key);

        return MessageColorizer.colorize(messages.get(key));
    }
}
