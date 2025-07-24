package mrquackduck.imageemojis.services.implementations;

import com.sun.net.httpserver.HttpServer;
import mrquackduck.imageemojis.services.abstractions.IHttpResourcePackServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class HttpResourcePackServer implements IHttpResourcePackServer {
    private final HttpServer server;

    public HttpResourcePackServer(int port, File resourcePackFile, String path) throws IOException {
        // Create HTTP server
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Define handler for all requests
        server.createContext(path, exchange -> {
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
