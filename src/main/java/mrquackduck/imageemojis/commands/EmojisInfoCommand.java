package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EmojisInfoCommand implements CommandExecutor {
    private final ImageEmojisPlugin plugin;

    public EmojisInfoCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int emojisCount = plugin.getEmojiRepository().getEmojis().size();
        String message = String.format(ImageEmojisPlugin.getMessage("info-content"), emojisCount);
        commandSender.sendMessage(message);
        return true;
    }
}
