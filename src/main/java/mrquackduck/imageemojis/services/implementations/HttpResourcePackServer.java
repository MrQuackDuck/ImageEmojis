package mrquackduck.imageemojis.services.implementations;

import com.sun.net.httpserver.HttpServer;
import mrquackduck.imageemojis.services.abstractions.IHttpResourcePackServer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class HttpResourcePackServer implements IHttpResourcePackServer {
    private final HttpServer server;

    public HttpResourcePackServer(Plugin plugin, int port, int threadCount, File resourcePackFile, String path) throws IOException {
        // Create HTTP server with multiple worker threads
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(threadCount));

        // Define handler for all requests
        server.createContext(path, exchange -> {
            try (exchange) {
                if (resourcePackFile.exists() && !resourcePackFile.isDirectory()) {
                    long fileLength = resourcePackFile.length();

                    // Add headers for content type and file name
                    exchange.getResponseHeaders().add("Content-Type", "application/zip");
                    exchange.getResponseHeaders().add("Content-Disposition",
                            "attachment; filename=\"" + resourcePackFile.getName() + "\"");

                    // Send response with the file
                    exchange.sendResponseHeaders(200, fileLength);
                    try (OutputStream os = exchange.getResponseBody()) {
                        Files.copy(resourcePackFile.toPath(), os);
                    }
                } else {
                    String errorMessage = "Resource pack file not found.";
                    byte[] errorBytes = errorMessage.getBytes();

                    exchange.sendResponseHeaders(404, errorBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(errorBytes);
                    }

                    plugin.getLogger().log(Level.SEVERE, errorMessage);
                }
            } catch (IOException e) {
                // Client disconnecting mid-download (broken pipe), not necessarily an issue
                plugin.getLogger().log(Level.WARNING, "Error handling resource pack request", e);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unexpected error handling resource pack request", e);
            }
        });
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
