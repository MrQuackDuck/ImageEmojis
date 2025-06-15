package mrquackduck.imageemojis.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.models.EmojiData;
import mrquackduck.imageemojis.utils.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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
        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();
        boolean hoverEnabled = config.isEmojiHoverEnabled();
        String hoverColor = config.emojiHoverColor();

        TextComponent messageComponent = (TextComponent)event.message();

        for (EmojiData emoji : emojis) {
            if (emoji.getChars().isEmpty()) continue;
            TextComponent replacement = Component.text(emoji.getAsUtf8Symbol());
            if (hoverEnabled) replacement = replacement.hoverEvent(HoverEvent.showText(Component.text(emoji.getTemplate()).color(TextColor.color(ColorUtil.hexToColor(hoverColor)))));

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

            messageComponent = (TextComponent) messageComponent
                    .replaceText(templateToUtf8ReplacementConfig)
                    .replaceText(utf8ToUtf8ReplacementConfig);
        }

        event.message(messageComponent);
    }
}
