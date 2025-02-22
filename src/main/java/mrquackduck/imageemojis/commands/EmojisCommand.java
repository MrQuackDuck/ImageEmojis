package mrquackduck.imageemojis.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
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

    public EmojisCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length == 0 || strings[0].equalsIgnoreCase("info")) {
            // Print info if command args not provided
            return new EmojisInfoCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("list") && commandSender.hasPermission("imageemojis.list")) {
            return new EmojisListCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("update") && commandSender.hasPermission("imageemojis.update")) {
            return new EmojisUpdateCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission("imageemojis.admin")) {
            return new EmojisReloadCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else {
            commandSender.sendMessage(ImageEmojisPlugin.getMessage("command-not-found"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        List<String> options = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        options.add("info");
        if (commandSender.hasPermission("imageemojis.list")) options.add("list");
        if (commandSender.hasPermission("imageemojis.update")) options.add("update");
        if (commandSender.hasPermission("imageemojis.admin")) options.add("reload");

        StringUtil.copyPartialMatches(args[0], options, completions);
        return completions;
    }
}
