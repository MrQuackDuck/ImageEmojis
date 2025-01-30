package mrquackduck.imageemojis.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mrquackduck.imageemojis.ImageEmojisPlugin;
import mrquackduck.imageemojis.interfaces.ResourcePackGenerator;
import mrquackduck.imageemojis.interfaces.ZipBuilder;
import mrquackduck.imageemojis.models.EmojiData;
import mrquackduck.imageemojis.models.ResourcePack;
import mrquackduck.imageemojis.serializable.Provider;
import mrquackduck.imageemojis.serializable.ProvidersWrapper;
import mrquackduck.imageemojis.utils.JsonUtil;
import mrquackduck.imageemojis.utils.PackFormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiResourcePackGenerator implements ResourcePackGenerator {
    private final ImageEmojisPlugin plugin;
    private final List<EmojiData> emojis;
    private final ZipBuilder zipBuilder;
    private final Gson gson;

    public EmojiResourcePackGenerator(ImageEmojisPlugin plugin) {
        this.plugin = plugin;
        this.emojis = plugin.getEmojiRepository().getEmojis();
        this.zipBuilder = new ZipFileBuilder();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public ResourcePack generate() {
        File pluginDirectory = plugin.getDataFolder().getAbsoluteFile();

        boolean mergeWithServerResourcePack = plugin.getConfig().getBoolean("mergeWithServerResourcePack");
        if (mergeWithServerResourcePack) {
            String mergeServerResourcePackName = plugin.getConfig().getString("mergeServerResourcePackName");
            String pathToResourcePackToMerge = pluginDirectory.getAbsolutePath() + "/" + mergeServerResourcePackName;
            zipBuilder.mergeWithZip(pathToResourcePackToMerge);
        }

        zipBuilder.addFile("/pack.mcmeta", generateMcPack());
        zipBuilder.addDirectory("/assets");
        zipBuilder.addDirectory("/assets/minecraft");
        zipBuilder.addDirectory("/assets/minecraft/font");
        zipBuilder.addDirectory("/assets/minecraft/textures");
        zipBuilder.addDirectory("/assets/minecraft/textures/font");
        String defaultJson = generateDefault();
        if (mergeWithServerResourcePack) {
            try { defaultJson = JsonUtil.mergeJsons(defaultJson, zipBuilder.getFileContent("/assets/minecraft/font/default.json")); }
            catch (RuntimeException ex) { /* Ignored */ }
        }
        zipBuilder.addFile("/assets/minecraft/font/default.json", defaultJson);
        copyEmojisToZip(); // Copy all emojis to zip
        copyPackPng(); // Copy resource pack icon to the root folder

        String path = pluginDirectory.getAbsolutePath() + "/emojis.zip";
        zipBuilder.writeToPath(path);

        byte[] hash = generateSHA1FromFile(new File(path));

        return new ResourcePack(path, hash, bytesToHex(hash));
    }

    private String generateMcPack() {
        String description = ImageEmojisPlugin.getMessage("resource-pack-description");

        // Create a map for the inner "pack" object
        Map<String, Object> pack = new HashMap<>();
        pack.put("description", description);
        pack.put("pack_format", PackFormatUtil.getPackFormat(plugin.getServer().getBukkitVersion()));

        // Create a map for the outer wrapper
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("pack", pack);

        return gson.toJson(wrapper);
    }

    private String generateDefault() {
        List<Provider> providers = new ArrayList<>();
        for (EmojiData emoji : emojis) {
            Provider provider = new Provider(
                    "bitmap",
                    "minecraft:font/" + emoji.getFileName(),
                    9,
                    10,
                    emoji.getChars());

            providers.add(provider);
        }

        ProvidersWrapper wrapper = new ProvidersWrapper(providers);

        String json = gson.toJson(wrapper);
        json = json.replace("\\\\", "\\");
        return json;
    }

    private void copyPackPng() {
        zipBuilder.copyFile(plugin.getDataFolder().getAbsolutePath() + "/pack.png", "/pack.png");
    }

    private void copyEmojisToZip() {
        String root = "/assets/minecraft/textures/font/";
        for (EmojiData emoji : emojis) {
            zipBuilder.copyFile(emoji.getAbsolutePath(), root + emoji.getFileName());
        }
    }

    private static byte[] generateSHA1FromFile(File file) {
        try {
            // Initialize MessageDigest for SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            // Read the file in chunks to avoid memory issues
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            // Generate the hash
            return digest.digest();
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b)); // Convert each byte to a 2-digit hex value
        }
        return hexString.toString();
    }
}
