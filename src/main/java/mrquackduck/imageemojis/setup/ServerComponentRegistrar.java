package mrquackduck.imageemojis.setup;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.server.commands.EmojisCommand;
import mrquackduck.imageemojis.server.listeners.*;

import java.util.Objects;

public class ServerComponentRegistrar {
    private final ImageEmojisPlugin plugin;

    public ServerComponentRegistrar(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        Objects.requireNonNull(plugin.getCommand("emojis")).setExecutor(new EmojisCommand(plugin));

        var manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new SendMessageListener(plugin), plugin);
        manager.registerEvents(new SendCommandListener(plugin), plugin);
        manager.registerEvents(new JoinListener(plugin), plugin);
        manager.registerEvents(new AnvilRenameListener(plugin), plugin);
        manager.registerEvents(new SignChangeListener(plugin), plugin);
    }
}
