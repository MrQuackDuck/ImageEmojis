package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.ResourcePack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmojisUpdateCommand implements CommandExecutor {
    private final ImageEmojisPlugin plugin;

    public EmojisUpdateCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ImageEmojisPlugin.getMessage("only-players"));
            return true;
        }

        String resourcePackDownloadUrl = plugin.getResourcePackDownloadUrl();
        ResourcePack resourcePack = plugin.getResourcePack();

        boolean enforceResourcePack = plugin.getConfig().getBoolean("enforceResourcePack");

        Player player = (Player) commandSender;
        player.setResourcePack(resourcePackDownloadUrl, resourcePack.getHash(), enforceResourcePack);

        player.sendMessage(ImageEmojisPlugin.getMessage("resource-pack-up-to-date"));

        return true;
    }
}
