package mrquackduck.imageemojis.services.abstractions;

import mrquackduck.imageemojis.types.models.EmojiModel;

import java.util.List;

public interface IEmojiRepository {
    List<EmojiModel> getEmojis();
}
