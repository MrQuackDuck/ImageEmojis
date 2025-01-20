package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.EmojiData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class SignChangeListener implements Listener {
    private final ImageEmojisPlugin plugin;

    public SignChangeListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChanged(SignChangeEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!plugin.getConfig().getBoolean("replaceInSigns")) return;

        Sign sign = (Sign) event.getBlock().getState();
        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();
        List<Component> lines = event.lines();

        for (int i = 0; i < lines.size(); i++) {
            TextComponent signLine = (TextComponent) lines.get(i);

            signLine = signLine.color(signLine.color());
            for (EmojiData emoji : emojis) {
                TextComponent replacement = Component.text(emoji.getAsUtf8Symbol()).color(TextColor.color(NamedTextColor.WHITE));

                // The replacement config to replace the emoji template to an actual emoji
                TextReplacementConfig templateToUtf8ReplacementConfig = TextReplacementConfig.builder()
                        .match(emoji.getTemplate())
                        .replacement(replacement)
                        .build();

                // The replacement config to fix existing UTF-8 symbols (to prevent them from being black on the sign)
                TextReplacementConfig utf8ToUtf8ReplacementConfig = TextReplacementConfig.builder()
                        .match(emoji.getAsUtf8Symbol())
                        .replacement(replacement)
                        .build();

                signLine = (TextComponent) signLine
                        .replaceText(templateToUtf8ReplacementConfig)
                        .replaceText(utf8ToUtf8ReplacementConfig);
            }

            event.line(i, signLine);
            sign.setEditable(true);
        }
    }
}
