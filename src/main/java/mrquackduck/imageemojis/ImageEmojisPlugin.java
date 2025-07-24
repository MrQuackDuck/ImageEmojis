package mrquackduck.imageemojis;

import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.services.abstractions.IEmojiRepository;
import mrquackduck.imageemojis.services.abstractions.IHttpResourcePackServer;
import mrquackduck.imageemojis.setup.HttpServerManager;
import mrquackduck.imageemojis.setup.PluginInitializer;
import mrquackduck.imageemojis.setup.ServerComponentRegistrar;
import mrquackduck.imageemojis.types.models.ResourcePack;
import mrquackduck.imageemojis.utils.MessageColorizer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mrquackduck.imageemojis.utils.EnvironmentUtil.isPaper;
import static mrquackduck.imageemojis.utils.EnvironmentUtil.isSpigot;
import static mrquackduck.imageemojis.utils.VersionComparer.isVersionHigherOrEqualThan;

public final class ImageEmojisPlugin extends JavaPlugin {
    private String resourcePackDownloadUrl = "";
    private IEmojiRepository emojiRepository;
    private IHttpResourcePackServer resourcePackServer;
    private ResourcePack resourcePack;
    private Logger logger;
    private final Configuration config = new Configuration(this);

    @Override
    public void onEnable() {
        logger = getLogger();

        if (isSpigot() && !isPaper()) {
            warnIncompatibleServer();
            return;
        }

        if (!isVersionHigherOrEqualThan(getServer().getBukkitVersion(), "1.19.4")) {
            warnOldVersion();
            return;
        }

        new ServerComponentRegistrar(this).registerAll();

        try { new PluginInitializer(this).initialize(); }
        catch (RuntimeException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        new HttpServerManager(this).startIfNeeded(resourcePack.getPath());
    }

    @Override
    public void onDisable() {
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            logger.info("Resource pack server stopped");
        }
    }

    public void reload() {
        reloadConfig();
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            logger.info("Resource pack server restarting...");
        }

        new PluginInitializer(this).initialize();
        new HttpServerManager(this).startIfNeeded(resourcePack.getPath());

        logger.info("Plugin restarted!");
    }

    private void warnIncompatibleServer() {
        getServer().getConsoleSender().sendMessage(MessageColorizer.colorize(
                "&c[ImageEmojis] Spigot detected! This plugin requires Paper 1.19.4 or later."
        ));
    }

    private void warnOldVersion() {
        getServer().getConsoleSender().sendMessage(MessageColorizer.colorize(
                "&c[ImageEmojis] Old version detected! Please update to Paper 1.19.4 or later."
        ));
    }

    public IEmojiRepository getEmojiRepository() { return emojiRepository; }
    public void setEmojiRepository(IEmojiRepository repo) { this.emojiRepository = repo; }

    public String getResourcePackDownloadUrl() { return resourcePackDownloadUrl; }
    public void setResourcePackDownloadUrl(String url) { this.resourcePackDownloadUrl = url; }

    public ResourcePack getResourcePack() { return resourcePack; }
    public void setResourcePack(ResourcePack pack) { this.resourcePack = pack; }

    public IHttpResourcePackServer getResourcePackServer() { return resourcePackServer; }
    public void setResourcePackServer(IHttpResourcePackServer server) { this.resourcePackServer = server; }

    public Configuration getConfigWrapper() { return config; }
}