package mrquackduck.imageemojis.services.implementations;

import mrquackduck.imageemojis.services.abstractions.IZipBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileBuilder implements IZipBuilder {
    private final Map<String, byte[]> inMemoryStructure;

    public ZipFileBuilder() {
        this.inMemoryStructure = new HashMap<>();
    }

    @Override
    public void addDirectory(String path) {
        path = normalizePath(path);
        if (!path.endsWith("/")) path += "/";
        inMemoryStructure.put(path, new byte[0]);
    }

    @Override
    public void addFile(String path, String content) {
        path = normalizePath(path);
        inMemoryStructure.put(path, content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getFileContent(String pathInZip) {
        pathInZip = normalizePath(pathInZip);
        byte[] content = inMemoryStructure.get(pathInZip);

        if (content == null) {
            throw new RuntimeException("File not found in zip: " + pathInZip);
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public void copyFile(String absolutePathInRealSystem, String pathInZip) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(absolutePathInRealSystem));
            pathInZip = normalizePath(pathInZip);
            inMemoryStructure.put(pathInZip, fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Error copying file: " + absolutePathInRealSystem, e);
        }
    }

    @Override
    public void mergeWithZip(String absolutePathInRealSystem) {
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(absolutePathInRealSystem)) {
            // Iterate through the entries of the existing ZIP file
            zipFile.stream().forEach(entry -> {
                try {
                    String normalizedPath = normalizePath(entry.getName());
                    if (entry.isDirectory()) {
                        // Add the directory to the in-memory structure
                        addDirectory(normalizedPath);
                    } else {
                        // Read file content and add it to the in-memory structure
                        byte[] content = zipFile.getInputStream(entry).readAllBytes();
                        inMemoryStructure.put(normalizedPath, content);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading zip entry: " + entry.getName(), e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error merging with zip file: " + absolutePathInRealSystem, e);
        }
    }

    @Override
    public void writeToPath(String path) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (Map.Entry<String, byte[]> entry : inMemoryStructure.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zos.putNextEntry(zipEntry);

                if (entry.getValue().length > 0) zos.write(entry.getValue());

                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing zip file: " + path, e);
        }
    }

    private String normalizePath(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }
}