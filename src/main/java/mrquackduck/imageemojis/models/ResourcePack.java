package mrquackduck.imageemojis.models;

public class ResourcePack {
    private final String path;
    private final byte[] hash;
    private final String hashString;

    public ResourcePack(String downloadUrl, byte[] hash, String hashString) {
        this.path = downloadUrl;
        this.hash = hash;
        this.hashString = hashString;
    }

    public String getPath() {
        return path;
    }

    public byte[] getHash() {
        return hash;
    }

    public String getHashString() {
        return hashString;
    }
}
