package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import mrquackduck.imageemojis.types.enums.SuggestionMode;
import mrquackduck.imageemojis.types.models.EmojiModel;
import mrquackduck.imageemojis.utils.SuggestionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class EmojisReloadCommand implements CommandExecutor {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public EmojisReloadCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        try {
            // Removing old suggestions
            SuggestionMode suggestionMode = config.suggestionMode();
            List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();

            for (Player player : plugin.getServer().getOnlinePlayers())
                SuggestionManager.removeSuggestions(player, emojis, suggestionMode);
        }
        catch (Exception e) { /* Ignored */ }

        // Signaling the plugin to reload
        try { plugin.reload(); }
        catch (Exception e) { return handleException(commandSender, e); }

        try {
            // Updating suggestions after reload
            SuggestionMode suggestionMode = config.suggestionMode();
            List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.hasPermission(Permissions.USE)) {
                    SuggestionManager.addSuggestions(player, emojis, suggestionMode);
                }
            }

            commandSender.sendMessage(config.getMessage("reloaded"));

            return true;
        }
        catch (Exception e) { return handleException(commandSender, e); }
    }

    private boolean handleException(@NotNull CommandSender commandSender, Exception e) {
        commandSender.sendMessage(config.getMessage("an-error-occurred"));
        plugin.getLogger().log(Level.SEVERE, e.getMessage());
        return true;
    }
}
