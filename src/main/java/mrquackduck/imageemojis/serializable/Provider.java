package mrquackduck.imageemojis.serializable;

import java.util.List;

public class Provider {
    private String type;
    private String file;
    private int ascent;
    private int height;
    private List<String> chars;

    public Provider(String type, String file, int ascent, int height, List<String> chars) {
        this.type = type;
        this.file = file;
        this.ascent = ascent;
        this.height = height;
        this.chars = chars;
    }
}
