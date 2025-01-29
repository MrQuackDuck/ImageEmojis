package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.enums.EnforcementPolicy;
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

        Player player = (Player) commandSender;

        String resourcePackDownloadUrl = plugin.getResourcePackDownloadUrl();
        ResourcePack resourcePack = plugin.getResourcePack();

        EnforcementPolicy enforcementPolicy = EnforcementPolicy.valueOf(plugin.getConfig().getString("enforcementPolicy"));
        if (enforcementPolicy == EnforcementPolicy.NONE) {
            player.sendMessage(ImageEmojisPlugin.getMessage("command-disabled"));
            return true;
        }

        player.setResourcePack(resourcePackDownloadUrl, resourcePack.getHash(), enforcementPolicy == EnforcementPolicy.REQUIRED);

        player.sendMessage(ImageEmojisPlugin.getMessage("resource-pack-up-to-date"));

        return true;
    }
}
