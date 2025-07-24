package mrquackduck.imageemojis.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.enums.NoPermAction;
import mrquackduck.imageemojis.models.EmojiModel;
import mrquackduck.imageemojis.utils.ColorUtil;
import mrquackduck.imageemojis.utils.TextComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class SendMessageListener implements Listener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public SendMessageListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessageSent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();
        TextComponent messageComponent = (TextComponent)event.message();

        for (EmojiModel emoji : emojis) {
            if (emoji.getChars().isEmpty()) continue;
            TextComponent replacement = Component.text(emoji.getAsUtf8Symbol());
            if (config.isEmojiHoverEnabled()) replacement = replacement.hoverEvent(HoverEvent.showText(Component.text(emoji.getTemplate()).color(TextColor.color(ColorUtil.hexToColor(config.emojiHoverColor())))));
            if (!player.hasPermission(Permissions.USE)) replacement = Component.empty();

            // The replacement config to replace the emoji template to an actual emoji
            TextReplacementConfig templateToUtf8ReplacementConfig = TextReplacementConfig.builder()
                    .match(emoji.getTemplate())
                    .replacement(replacement)
                    .build();

            // The replacement config to apply the hover effect on UTF-8 symbols (if are present in the chat)
            TextReplacementConfig utf8ToUtf8ReplacementConfig = TextReplacementConfig.builder()
                    .match(emoji.getAsUtf8Symbol())
                    .replacement(replacement)
                    .build();

            if (player.hasPermission(Permissions.USE))
                messageComponent = (TextComponent) messageComponent.replaceText(templateToUtf8ReplacementConfig);

            messageComponent = (TextComponent) messageComponent.replaceText(utf8ToUtf8ReplacementConfig);
        }

        messageComponent = TextComponentUtil.trim(messageComponent);

        event.message(messageComponent);
        if (TextComponentUtil.getFullContent(messageComponent).trim().isEmpty()) event.setCancelled(true);
    }

    /**
     * An event handler compatible with Spigot chat formatters
     */
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessageSent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();
        String message = event.getMessage();

        for (EmojiModel emoji : emojis) {
            if (emoji.getChars().isEmpty()) continue;
            if (!player.hasPermission(Permissions.USE) && config.inChatNoPermAction() == NoPermAction.CANCEL_EVENT
                    && message.contains(emoji.getAsUtf8Symbol())) {
                if (config.shouldNoPermMessageAppear()) player.sendMessage(config.getMessage("not-enough-permissions"));
                event.setCancelled(true);
                return;
            }

            if (!player.hasPermission(Permissions.USE)) {
                message = message.replace(emoji.getAsUtf8Symbol(), "");
                continue;
            }

            message = message.replace(emoji.getTemplate(), emoji.getAsUtf8Symbol());
        }

        // Remove extra spaces
        message = message.trim();

        event.setMessage(message);
        if (message.isEmpty()) event.setCancelled(true);
    }
}
