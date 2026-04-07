import java.io.*;
import java.net.*;
import java.nio.file.*;

public class SimpleHttpServer {
    private static final int PORT = 8000;
    private static final String WEB_ROOT = System.getProperty("user.dir");

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("MatriDisha Main Portal Server started on http://localhost:" + PORT);
            System.out.println("Web root: " + WEB_ROOT);
            System.out.println("Press Ctrl+C to stop the server");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            OutputStream out = clientSocket.getOutputStream();

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.trim().isEmpty()) {
                clientSocket.close();
                return;
            }

            // Read and ignore remaining headers
            String line;
            while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                clientSocket.close();
                return;
            }

            String path = requestParts[1];
            
            // Remove query parameters
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                path = path.substring(0, queryIndex);
            }
            
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(WEB_ROOT + path);
            
            if (file.exists() && file.isFile()) {
                String contentType = getContentType(path);
                byte[] fileContent = Files.readAllBytes(file.toPath());
                
                String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n" +
                    "Connection: close\r\n\r\n";
                
                out.write(response.getBytes());
                out.write(fileContent);
            } else {
                String errorResponse = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Connection: close\r\n\r\n" +
                    "<html><body><h1>404 - File Not Found</h1></body></html>";
                out.write(errorResponse.getBytes());
            }

            out.flush();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".json")) {
            return "application/json";
        } else {
            return "text/plain";
        }
    }
}
