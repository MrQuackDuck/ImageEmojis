package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.enums.NoPermAction;
import mrquackduck.imageemojis.models.EmojiData;
import mrquackduck.imageemojis.utils.TextComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;

public class AnvilRenameListener implements Listener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public AnvilRenameListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @EventHandler
    public void onItemRenamedInAnvil(PrepareAnvilEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!config.isAnvilReplacementEnabled()) return;

        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();
        Player player = (Player)event.getView().getPlayer();

        TextComponent displayName = getItemStackName(event.getResult());
        if (displayName == null) return;
        final String componentContent = TextComponentUtil.getFullContent(displayName);

        for (EmojiData emoji : emojis) {
            TextComponent replacement = Component.text(emoji.getAsUtf8Symbol()).color(NamedTextColor.WHITE);
            if (!player.hasPermission(Permissions.USE) && config.inAnvilsNoPermAction() == NoPermAction.CANCEL_EVENT
                    && componentContent.contains(emoji.getAsUtf8Symbol())) {
                if (config.shouldNoPermMessageAppear()) player.sendMessage(config.getMessage("not-enough-permissions"));
                event.setResult(null);
                return;
            }

            if (!player.hasPermission(Permissions.USE)) replacement = Component.empty();

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

            if (player.hasPermission(Permissions.USE))
                displayName = (TextComponent) displayName.replaceText(templateToUtf8ReplacementConfig);

            displayName = (TextComponent) displayName.replaceText(utf8ToUtf8ReplacementConfig);
        }

        if (TextComponentUtil.getFullContent(displayName).trim().isEmpty()) {
            event.setResult(null);
            return;
        }

        ItemMeta resultMeta = event.getResult().getItemMeta();
        resultMeta.displayName(displayName);
        event.getResult().setItemMeta(resultMeta);
    }

    private static @Nullable TextComponent getItemStackName(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        return (TextComponent) meta.displayName();
    }
}
