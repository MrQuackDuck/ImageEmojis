package mrquackduck.imageemojis.models;

import mrquackduck.imageemojis.utils.CharUtil;

import java.util.List;

public class EmojiModel {
    private final String name;
    private final String fileName;
    private final int height;
    private final String absolutePath;
    private final List<String> chars;
    private final String templateFormat;

    public EmojiModel(String name, String fileName, int height, String absolutePath, List<String> chars, String templateFormat) {
        this.name = name;
        this.fileName = fileName;
        this.height = height;
        this.absolutePath = absolutePath;
        this.chars = chars;
        this.templateFormat = templateFormat;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return templateFormat.replace("<emoji>", getName());
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
        return CharUtil.parseUtf8CodeToActualSymbol(getChars().get(0));
    }
}
