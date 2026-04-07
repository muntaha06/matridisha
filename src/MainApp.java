import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

// Main Application Class
public class MainApp {

    // Main Method (Server Start)
    public static void main(String[] args) throws IOException {

        // Create HTTP Server (Port 8083)
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);

        // Static File Handler (HTML, CSS, Images)
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();

            // Default Route Handling
            if (path.equals("/")) {
                path = "/index.html";
            }

            // File Path Setup (web folder)
            File file = new File("web" + path);

            // File Exists Check & Response
            if (file.exists() && !file.isDirectory()) {
                byte[] content = Files.readAllBytes(file.toPath());

                // Content-Type set
                if (path.endsWith(".html"))
                    ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

                if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                    ex.getResponseHeaders().set("Content-Type", "image/jpeg");

                if (path.endsWith(".png"))
                    ex.getResponseHeaders().set("Content-Type", "image/png");

                if (path.endsWith(".css"))
                    ex.getResponseHeaders().set("Content-Type", "text/css");

                // Send Successful Response (200)
                ex.sendResponseHeaders(200, content.length);
                OutputStream os = ex.getResponseBody();
                os.write(content);
                os.close();

            } else {
                // Handle File Not Found (404)
                String response = "404 (Not Found): ফাইলটি খুঁজে পাওয়া যায়নি।";
                ex.sendResponseHeaders(404, response.length());
                OutputStream os = ex.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        // Server Executor & Start
        server.setExecutor(null);
        server.start();

        // Console Output
        System.out.println("Server started successfully!");
        System.out.println("Visit: http://localhost:8083/diseases.html");
    }
}