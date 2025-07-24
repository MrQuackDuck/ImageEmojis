package mrquackduck.imageemojis.setup;

import github.scarsz.discordsrv.DiscordSRV;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.discord.listeners.SendDiscordMessageListener;
import mrquackduck.imageemojis.discord.listeners.SendGameMessageListener;

public class DiscordRegistrar {
    private final ImageEmojisPlugin plugin;

    public DiscordRegistrar(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerIntegration() {
        if (!isHooked()) return;

        DiscordSRV.api.subscribe(new SendDiscordMessageListener(plugin));
        DiscordSRV.api.subscribe(new SendGameMessageListener(plugin));
    }

    private static boolean isHooked() {
        try {
            Class.forName("github.scarsz.discordsrv.DiscordSRV");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}