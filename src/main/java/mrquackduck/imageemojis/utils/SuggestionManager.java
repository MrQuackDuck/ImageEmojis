package mrquackduck.imageemojis.utils;

import mrquackduck.imageemojis.enums.SuggestionMode;
import mrquackduck.imageemojis.models.EmojiData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SuggestionManager {
    public static void addSuggestions(Player player, List<EmojiData> emojis, SuggestionMode suggestionMode) {
        player.addCustomChatCompletions(getSuggestionsList(emojis, suggestionMode));
    }

    public static void removeSuggestions(Player player, List<EmojiData> emojis, SuggestionMode suggestionMode) {
        player.removeCustomChatCompletions(getSuggestionsList(emojis, suggestionMode));
    }

    private static List<String> getSuggestionsList(List<EmojiData> emojis, SuggestionMode suggestionMode) {
        ArrayList<String> suggestions = new ArrayList<>();
        for (EmojiData emoji : emojis) {
            if (suggestionMode == SuggestionMode.TEMPLATES || suggestionMode == SuggestionMode.BOTH) suggestions.add(emoji.getTemplate());
            if (suggestionMode == SuggestionMode.ACTUAL || suggestionMode == SuggestionMode.BOTH) suggestions.add(emoji.getAsUtf8Symbol());
        }

        return suggestions;
    }
}
