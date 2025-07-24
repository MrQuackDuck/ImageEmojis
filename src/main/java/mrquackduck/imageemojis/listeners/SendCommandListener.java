package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.types.enums.NoPermAction;
import mrquackduck.imageemojis.types.models.EmojiModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class SendCommandListener implements Listener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public SendCommandListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @EventHandler
    public void onCommandPrepared(PlayerCommandPreprocessEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!config.isCommandReplacementEnabled()) return;

        Player player = event.getPlayer();
        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();

        String command = event.getMessage();
        for (EmojiModel emoji : emojis) {
            if (!player.hasPermission(Permissions.USE) && config.inCommandsNoPermAction() == NoPermAction.CANCEL_EVENT
                    && command.contains(emoji.getAsUtf8Symbol())) {
                if (config.shouldNoPermMessageAppear()) player.sendMessage(config.getMessage("not-enough-permissions"));
                event.setCancelled(true);
                return;
            }

            if (!event.getPlayer().hasPermission(Permissions.USE)) {
                command = command.replace(emoji.getAsUtf8Symbol(), "");
                continue;
            }

            command = command.replace(emoji.getTemplate(), emoji.getAsUtf8Symbol());
        }

        event.setMessage(command);
    }
}
