package mrquackduck.imageemojis.setup;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.services.abstractions.IHttpResourcePackServer;
import mrquackduck.imageemojis.services.implementations.HttpResourcePackServer;
import mrquackduck.imageemojis.types.enums.EnforcementPolicy;

import java.io.File;
import java.io.IOException;

public class HttpServerManager {
    private final ImageEmojisPlugin plugin;

    public HttpServerManager(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    public void startIfNeeded(String resourcePackPath) {
        Configuration config = plugin.getConfigWrapper();
        if (config.enforcementPolicy() == EnforcementPolicy.NONE) {
            plugin.getLogger().info("Enforcement policy set to NONE: No need to start the resource pack HTTP server.");
            return;
        }

        try {
            File resourcePackFile = new File(resourcePackPath);
            IHttpResourcePackServer server = new HttpResourcePackServer(
                    config.webServerPort(),
                    resourcePackFile,
                    "/" + plugin.getResourcePack().getHashString()
            );

            server.start();
            plugin.setResourcePackServer(server);

            String url = "http://" + config.serverIp() + ":" + config.webServerPort() + "/" + plugin.getResourcePack().getHashString();
            plugin.setResourcePackDownloadUrl(url);
            plugin.getLogger().info("Resource pack server started at " + url);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start HTTP server: " + e.getMessage());
        }
    }
}
