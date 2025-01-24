package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.EmojiData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AnvilRenameListener implements Listener {
    private final ImageEmojisPlugin plugin;

    public AnvilRenameListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemRenamedInAnvil(PrepareAnvilEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!plugin.getConfig().getBoolean("replaceInAnvil")) return;

        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();

        ItemStack itemStack = event.getResult();
        if (itemStack == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        TextComponent displayName = (TextComponent) meta.displayName();
        for (EmojiData emoji : emojis) {
            TextComponent replacement = Component.text(emoji.getAsUtf8Symbol()).color(NamedTextColor.WHITE);

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

            if (displayName == null) continue;
            displayName = (TextComponent) displayName
                    .replaceText(templateToUtf8ReplacementConfig)
                    .replaceText(utf8ToUtf8ReplacementConfig);
        }

        meta.displayName(displayName);
        itemStack.setItemMeta(meta);
    }
}
