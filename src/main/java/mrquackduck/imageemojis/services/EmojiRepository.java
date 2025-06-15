package mrquackduck.imageemojis.services;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.models.EmojiData;
import mrquackduck.imageemojis.utils.CharUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class EmojiRepository {
    private final ImageEmojisPlugin plugin;
    private final Logger logger;
    private List<EmojiData> cachedEmojis;

    public EmojiRepository(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.cachedEmojis = null;
    }

    public List<EmojiData> getEmojis() {
        if (cachedEmojis != null) return cachedEmojis;

        File emojisFolder = new File(plugin.getDataFolder(), "emojis");

        File[] files = emojisFolder.listFiles();

        List<EmojiData> emojis = new ArrayList<>();
        if (files == null) return emojis;

        long rangeStart = CharUtil.fromUtf8Code("\\uEff2");
        long rangeEnd = rangeStart + 2000;

        for (File file : files) {
            if (!file.isFile() || !isImageFile(file)) continue;

            try {
                BufferedImage image = ImageIO.read(file);
                if (image == null) continue;

                String name = file.getName().substring(0, file.getName().lastIndexOf('.')).toLowerCase();
                String fileName = file.getName().toLowerCase();

                // Generating a hash based on the file name
                String fileNameHash = CharUtil.generateSHA256(fileName);

                // Applying the hash on certain UTF-8 range in order to get a unique UTF-8 code for the emoji
                String utf8Code = CharUtil.toUtf8Code(CharUtil.hashToRange(fileNameHash, rangeStart, rangeEnd));

                int height = image.getHeight();
                String absolutePath = file.getAbsolutePath();

                EmojiData emojiData = new EmojiData(name, fileName, height, absolutePath, Collections.singletonList(utf8Code));
                emojis.add(emojiData);
            } catch (IOException e) {
                logger.warning("Failed to read image file: " + file.getName());
            }
        }

        // Sort emojis by name
        emojis.sort(Comparator.comparing(EmojiData::getName));

        cachedEmojis = emojis;
        return emojis;
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }
}
