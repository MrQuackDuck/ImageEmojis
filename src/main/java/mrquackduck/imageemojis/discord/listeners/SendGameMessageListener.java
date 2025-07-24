package mrquackduck.imageemojis.discord.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.types.models.EmojiModel;
import org.bukkit.entity.Player;

import java.util.List;

public class SendGameMessageListener {
    private final ImageEmojisPlugin plugin;

    public SendGameMessageListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onMinecraftMessageReceived(GameChatMessagePostProcessEvent event) {
        List<EmojiModel> gameEmojis = plugin.getEmojiRepository().getEmojis();
        String processedMessage = event.getProcessedMessage();
        Player player = event.getPlayer();

        for (EmojiModel emoji : gameEmojis) {
            if (emoji.getChars().isEmpty()) continue;
            String defaultTemplate = ':' + emoji.getName() + ':';
            String replacement = defaultTemplate;

            // Escape emotes if player doesn't have enough permissions
            if (!player.hasPermission(Permissions.USE)) {
                replacement = "\\:" + emoji.getName() + "\\:";
                processedMessage = processedMessage.replace(defaultTemplate, replacement);
                continue;
            }

            processedMessage = processedMessage
                    .replace(emoji.getTemplate(), replacement)
                    .replace(emoji.getAsUtf8Symbol(), replacement);
        }

        event.setProcessedMessage(processedMessage);
    }
}
