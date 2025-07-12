package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.configuration.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EmojisCommand implements CommandExecutor, TabCompleter {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public EmojisCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length == 0 || strings[0].equalsIgnoreCase("info")) {
            // Print info if command args were not provided
            return new EmojisInfoCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("list") && commandSender.hasPermission(Permissions.LIST)) {
            return new EmojisListCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("update") && commandSender.hasPermission(Permissions.UPDATE)) {
            return new EmojisUpdateCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission(Permissions.ADMIN)) {
            return new EmojisReloadCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else {
            commandSender.sendMessage(config.getMessage("command-not-found"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        List<String> options = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        options.add("info");
        if (commandSender.hasPermission(Permissions.LIST)) options.add("list");
        if (commandSender.hasPermission(Permissions.UPDATE)) options.add("update");
        if (commandSender.hasPermission(Permissions.ADMIN)) options.add("reload");

        StringUtil.copyPartialMatches(args[0], options, completions);
        return completions;
    }
}
