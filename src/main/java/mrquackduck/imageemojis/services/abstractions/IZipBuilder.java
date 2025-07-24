package mrquackduck.imageemojis.services.abstractions;

public interface IZipBuilder {
    void addDirectory(String path);
    void addFile(String path, String content);
    String getFileContent(String pathInZip);
    void copyFile(String absolutePathInRealSystem, String pathInZip);
    void mergeWithZip(String absolutePathInRealSystem);
    void writeToPath(String path);
}
