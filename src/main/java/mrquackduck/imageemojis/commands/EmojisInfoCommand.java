package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EmojisInfoCommand implements CommandExecutor {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public EmojisInfoCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int emojisCount = plugin.getEmojiRepository().getEmojis().size();
        String message = String.format(config.getMessage("info-content"), emojisCount);
        commandSender.sendMessage(message);
        return true;
    }
}
