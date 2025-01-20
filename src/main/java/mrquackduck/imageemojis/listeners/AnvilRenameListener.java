package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.EmojiData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
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
            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                    .match(emoji.getTemplate())
                    .replacement(Component.text(emoji.getAsUtf8Symbol()))
                    .build();

            if (displayName == null) continue;
            displayName = (TextComponent) displayName.replaceText(replacementConfig);
        }

        meta.displayName(displayName);
        itemStack.setItemMeta(meta);
    }
}
