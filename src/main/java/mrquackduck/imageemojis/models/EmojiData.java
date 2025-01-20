package mrquackduck.imageemojis.models;

import mrquackduck.imageemojis.utils.CharUtil;

import java.util.List;

public class EmojiData {
    private final String name;
    private final String fileName;
    private final int height;
    private final String absolutePath;
    private final List<String> chars;

    public EmojiData(String name, String fileName, int height, String absolutePath, List<String> chars) {
        this.name = name;
        this.fileName = fileName;
        this.height = height;
        this.absolutePath = absolutePath;
        this.chars = chars;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return ':' + getName() + ':';
    }

    public String getFileName() {
        return fileName;
    }

    public int getHeight() {
        return height;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public List<String> getChars() {
        return chars;
    }

    public String getAsUtf8Symbol() {
        return CharUtil.toUtf8Code(getChars().get(0));
    }
}
