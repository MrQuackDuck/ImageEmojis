package mrquackduck.imageemojis.interfaces;

public interface ZipBuilder {
    void addDirectory(String path);
    void addFile(String path, String content);
    void copyFile(String absolutePathInRealSystem, String pathInZip);
    void mergeWithZip(String absolutePathInRealSystem);
    void writeToPath(String path);
}
