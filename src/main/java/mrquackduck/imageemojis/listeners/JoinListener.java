package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.enums.EnforcementPolicy;
import mrquackduck.imageemojis.enums.SuggestionMode;
import mrquackduck.imageemojis.models.ResourcePack;
import mrquackduck.imageemojis.utils.SuggestionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    ImageEmojisPlugin plugin;

    public JoinListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String resourcePackDownloadUrl = plugin.getResourcePackDownloadUrl();
        ResourcePack resourcePack = plugin.getResourcePack();

        // Checking enforcement policy
        EnforcementPolicy enforcementPolicy = EnforcementPolicy.valueOf(plugin.getConfig().getString("enforcementPolicy"));
        if (enforcementPolicy != EnforcementPolicy.NONE)
            player.setResourcePack(resourcePackDownloadUrl, resourcePack.getHash(), enforcementPolicy == EnforcementPolicy.REQUIRED);

        SuggestionMode suggestionMode = SuggestionMode.valueOf(plugin.getConfig().getString("suggestionMode"));

        if (suggestionMode == SuggestionMode.NONE) return;
        SuggestionManager.addSuggestions(player, plugin.getEmojiRepository().getEmojis(), suggestionMode);
    }
}
