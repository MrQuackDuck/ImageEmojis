package mrquackduck.imageemojis.services;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class HttpResourcePackServer {
    private final HttpServer server;

    public HttpResourcePackServer(int port, File resourcePackFile) throws IOException {
        // Create HTTP server
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Define handler for all requests
        server.createContext("/", exchange -> {
            if (resourcePackFile.exists() && !resourcePackFile.isDirectory()) {
                byte[] fileBytes = Files.readAllBytes(resourcePackFile.toPath());

                // Add headers for content type and file name
                exchange.getResponseHeaders().add("Content-Type", "application/zip");
                exchange.getResponseHeaders().add("Content-Disposition",
                        "attachment; filename=\"" + resourcePackFile.getName() + "\"");

                // Send response with the file
                exchange.sendResponseHeaders(200, fileBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }
            } else {
                String errorMessage = "Resource pack file not found.";
                exchange.sendResponseHeaders(404, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
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
