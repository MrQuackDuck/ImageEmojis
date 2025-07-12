package mrquackduck.imageemojis.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;

public class TextComponentUtil {
    public static String getFullContent(Component component) {
        StringBuilder contentBuilder = new StringBuilder();

        if (component instanceof TextComponent)
            contentBuilder.append(((TextComponent) component).content());

        for (Component child : component.children()) {
            // Recursively append child content
            contentBuilder.append(getFullContent(child));
        }

        return contentBuilder.toString();
    }

    public static TextComponent trim(TextComponent component) {
        TextReplacementConfig removeLeadingSpaces = TextReplacementConfig.builder()
                .match("^\\s{2,}")
                .replacement(Component.empty())
                .build();

        TextReplacementConfig removeTrailingSpaces = TextReplacementConfig.builder()
                .match("\\s{2,}$")
                .replacement(Component.empty())
                .build();

        component = (TextComponent) component
                .replaceText(removeLeadingSpaces)
                .replaceText(removeTrailingSpaces);

        return component;
    }

    public static boolean areEqual(Component firstComponent, Component secondComponent) {
        return getFullContent(firstComponent).equals(getFullContent(secondComponent));
    }
}
