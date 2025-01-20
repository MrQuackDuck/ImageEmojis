package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.enums.SuggestionMode;
import mrquackduck.imageemojis.models.EmojiData;
import mrquackduck.imageemojis.utils.MessageColorizer;
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

    public EmojisReloadCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        // Removing old suggestions
        SuggestionMode suggestionMode = SuggestionMode.valueOf(plugin.getConfig().getString("suggestionMode"));
        List<EmojiData> emojis = plugin.getEmojiRepository().getEmojis();

        for (Player player : plugin.getServer().getOnlinePlayers())
            SuggestionManager.removeSuggestions(player, emojis, suggestionMode);

        // Signaling the plugin to reload
        try { plugin.reload(); }
        catch (RuntimeException e) {
            commandSender.sendMessage(ImageEmojisPlugin.getMessage("an-error-occurred"));
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            return true;
        }

        // Updating suggestions after reload
        suggestionMode = SuggestionMode.valueOf(plugin.getConfig().getString("suggestionMode"));
        emojis = plugin.getEmojiRepository().getEmojis();
        for (Player player : plugin.getServer().getOnlinePlayers())
            SuggestionManager.addSuggestions(player, emojis, suggestionMode);

        commandSender.sendMessage(MessageColorizer.colorize(ImageEmojisPlugin.getMessage("reloaded")));

        return true;
    }
}
