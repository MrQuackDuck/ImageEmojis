package mrquackduck.imageemojis.server.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.types.enums.EnforcementPolicy;
import mrquackduck.imageemojis.types.enums.SuggestionMode;
import mrquackduck.imageemojis.types.models.ResourcePack;
import mrquackduck.imageemojis.utils.SuggestionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public JoinListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String resourcePackDownloadUrl = plugin.getResourcePackDownloadUrl();
        ResourcePack resourcePack = plugin.getResourcePack();

        // Checking enforcement policy
        EnforcementPolicy enforcementPolicy = config.enforcementPolicy();
        if (enforcementPolicy != EnforcementPolicy.NONE)
            player.setResourcePack(resourcePackDownloadUrl, resourcePack.getHash(), enforcementPolicy == EnforcementPolicy.REQUIRED);

        SuggestionMode suggestionMode = config.suggestionMode();

        if (suggestionMode == SuggestionMode.NONE) return;
        if (!player.hasPermission(Permissions.USE)) return;
        SuggestionManager.addSuggestions(player, plugin.getEmojiRepository().getEmojis(), suggestionMode);
    }
}
