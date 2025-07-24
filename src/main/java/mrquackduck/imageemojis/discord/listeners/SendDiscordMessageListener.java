package mrquackduck.imageemojis.discord.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextComponent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.format.TextColor;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.types.models.EmojiModel;
import mrquackduck.imageemojis.utils.ColorUtil;

import java.util.List;

public final class SendDiscordMessageListener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public SendDiscordMessageListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Subscribe
    public void onGuildMessageReceived(DiscordGuildMessagePostProcessEvent event) {
        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();
        TextComponent messageComponent = (TextComponent) event.getMinecraftMessage();

        for (EmojiModel emoji : emojis) {
            if (emoji.getChars().isEmpty()) continue;
            TextComponent replacement = Component.text(emoji.getAsUtf8Symbol());
            if (config.isEmojiHoverEnabled()) replacement = replacement.hoverEvent(HoverEvent.showText(Component.text(emoji.getTemplate()).color(TextColor.color(ColorUtil.hexToColor(config.emojiHoverColor())))));

            // The replacement config to replace the emoji template to an actual emoji
            TextReplacementConfig templateToUtf8ReplacementConfig = TextReplacementConfig.builder()
                    .match(':' + emoji.getName() + ':')
                    .replacement(replacement)
                    .build();

            // The replacement config to apply the hover effect on UTF-8 symbols (if are present)
            TextReplacementConfig utf8ToUtf8ReplacementConfig = TextReplacementConfig.builder()
                    .match(emoji.getAsUtf8Symbol())
                    .replacement(replacement)
                    .build();

            messageComponent = (TextComponent) messageComponent
                    .replaceText(templateToUtf8ReplacementConfig)
                    .replaceText(utf8ToUtf8ReplacementConfig);
        }

        event.setMinecraftMessage(messageComponent);
    }
}
