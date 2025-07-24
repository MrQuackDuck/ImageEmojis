package mrquackduck.imageemojis.server.commands;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.types.models.EmojiModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EmojisListCommand implements CommandExecutor {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;

    public EmojisListCommand(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(config.getMessage("only-players"));
            return true;
        }

        Player player = (Player) commandSender;
        player.openBook(getEmojisBook());

        return false;
    }

    private ItemStack getEmojisBook() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta)itemStack.getItemMeta();

        List<EmojiModel> emojis = plugin.getEmojiRepository().getEmojis();

        List<Component> pages = getPages(emojis);

        bookMeta = bookMeta.toBuilder()
                .author(Component.text(""))
                .title(Component.text(""))
                .pages(pages).build();

        itemStack.setItemMeta(bookMeta);
        return itemStack;
    }

    private static @NotNull List<Component> getPages(List<EmojiModel> emojis) {
        TextComponent textComponent = Component.text("");
        List<Component> pages = new ArrayList<>();
        int row = 0;
        for (EmojiModel emoji : emojis) {
            if (row >= 14) {
                pages.add(textComponent);
                textComponent = Component.text("");
                row = 0;
            }

            textComponent = textComponent.append(Component.text(emoji.getAsUtf8Symbol()).color(NamedTextColor.WHITE))
                    .append(Component.text(emoji.getTemplate()).color(NamedTextColor.BLACK))
                    .append(Component.text("\n\n"));

            row += 2;
        }

        pages.add(textComponent);

        return pages;
    }
}
