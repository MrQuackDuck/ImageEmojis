package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.EmojiData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class SendCommandListener implements Listener {
    private final ImageEmojisPlugin plugin;

    public SendCommandListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPrepared(PlayerCommandPreprocessEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!plugin.getConfig().getBoolean("replaceInCommands")) return;

        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();

        String command = event.getMessage();
        for (EmojiData emoji : emojis) command = command.replace(emoji.getTemplate(), emoji.getAsUtf8Symbol());

        event.setMessage(command);
    }
}
