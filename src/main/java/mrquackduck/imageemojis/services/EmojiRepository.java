package mrquackduck.imageemojis.services;

import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.configuration.Configuration;
import mrquackduck.imageemojis.types.models.EmojiModel;
import mrquackduck.imageemojis.utils.CharUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class EmojiRepository {
    private final ImageEmojisPlugin plugin;
    private final Configuration config;
    private final Logger logger;
    private List<EmojiModel> cachedEmojis;

    public EmojiRepository(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
        this.logger = plugin.getLogger();
        this.cachedEmojis = null;
    }

    public List<EmojiModel> getEmojis() {
        if (cachedEmojis != null) return cachedEmojis;

        File emojisFolder = new File(plugin.getDataFolder(), "emojis");

        File[] files = emojisFolder.listFiles();

        List<EmojiModel> emojis = new ArrayList<>();
        if (files == null) return emojis;

        long rangeStart;
        long rangeEnd;

        if (config.isExtendedUnicodeRangeEnabled()) {
            rangeStart = CharUtil.parseUtf8CodeToLong("\\uE000");
            rangeEnd = rangeStart + 6400;
        }
        else {
            // Setting these values for backward compatibility
            rangeStart = CharUtil.parseUtf8CodeToLong("\\uEff2");
            rangeEnd = rangeStart + 2000;
        }

        for (File file : files) {
            if (!file.isFile()) continue;
            if (!isPng(file)) {
                logger.warning(String.format("Skipping '%s'. Only 'png'-native image files are supported. Try converting into 'png'.", file.getName()));
                continue;
            }

            try {
                BufferedImage image = ImageIO.read(file);
                if (image == null) continue;

                String name = file.getName().substring(0, file.getName().lastIndexOf('.')).toLowerCase();
                String fileName = file.getName().toLowerCase();

                // Generating a hash based on the file name
                String fileNameHash = CharUtil.generateSHA256(fileName);

                // Applying the hash on certain UTF-8 range in order to get a unique UTF-8 code for the emoji
                String utf8Code = CharUtil.parseLongToUtf8Code(CharUtil.hashToRange(fileNameHash, rangeStart, rangeEnd));

                int height = image.getHeight();
                String absolutePath = file.getAbsolutePath();

                EmojiModel emojiModel = new EmojiModel(name, fileName, height, absolutePath, Collections.singletonList(utf8Code), config.templateFormat());
                emojis.add(emojiModel);
            } catch (IOException e) {
                logger.warning("Failed to read image file: " + file.getName());
            }
        }

        // Sort emojis by name
        emojis.sort(Comparator.comparing(EmojiModel::getName));

        cachedEmojis = emojis;
        return emojis;
    }

    private boolean isPng(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png");
    }
}
