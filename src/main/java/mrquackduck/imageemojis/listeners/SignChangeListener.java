package mrquackduck.imageemojis.listeners;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.types.enums.NoPermAction;
import mrquackduck.imageemojis.types.models.EmojiModel;
import mrquackduck.imageemojis.utils.TextComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class SignChangeListener implements Listener {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public SignChangeListener(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @EventHandler
    public void onSignChanged(SignChangeEvent event) {
        // Prevent execution if this feature is disabled in the config
        if (!config.isSignReplacementEnabled()) return;

        Player player = event.getPlayer();
        Sign sign = (Sign) event.getBlock().getState();

        // Sign's side before it was edited
        SignSide originalSide = sign.getSide(event.getSide());
        List<Component> originalSideLines = originalSide.lines();

        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();
        List<Component> lines = event.lines();

        boolean shouldNoPermMessageAppear = false;

        for (int i = 0; i < lines.size(); i++) {
            TextComponent signLine = (TextComponent) lines.get(i);
            if (TextComponentUtil.areEqual(signLine, originalSideLines.get(i))) {
                // Leave the line as it is if nothing has changed
                event.line(i, originalSideLines.get(i));
                continue;
            }

            final String componentContent = TextComponentUtil.getFullContent(signLine);
            signLine = signLine.color(signLine.color());
            for (EmojiModel emoji : emojis) {
                if (!player.hasPermission(Permissions.USE) && config.onSignsNoPermAction() == NoPermAction.CANCEL_EVENT
                        && componentContent.contains(emoji.getAsUtf8Symbol())) {
                    if (config.shouldNoPermMessageAppear()) shouldNoPermMessageAppear = true;
                    // Leave the line as it is if the player doesn't have the "imageemojis.use" permission
                    // and "noPermAction.onSigns" is set to "CANCEL_EVENT"
                    signLine = (TextComponent) originalSideLines.get(i);
                    break;
                }

                TextComponent replacement = Component.text(emoji.getAsUtf8Symbol()).color(TextColor.color(NamedTextColor.WHITE));
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
                    signLine = (TextComponent) signLine.replaceText(templateToUtf8ReplacementConfig);

                signLine = (TextComponent) signLine.replaceText(utf8ToUtf8ReplacementConfig);
            }

            event.line(i, signLine);
            sign.setEditable(true);
        }

        if (shouldNoPermMessageAppear) player.sendMessage(config.getMessage("not-enough-permissions"));
    }
}
