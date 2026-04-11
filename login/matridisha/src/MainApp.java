import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class MainApp {

    // ===============================
    // 1️⃣ Global Variable (Session Tracking)
    // ===============================
    // Stores currently logged-in username
    private static String loggedInUser = "";

    // ===============================
    // 2️⃣ Main Method – Start HTTP Server
    // ===============================
    public static void main(String[] args) throws IOException {

        // Create HTTP Server on port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // ===============================
        // 3️⃣ Static File Server (HTML, CSS, JS)
        // ===============================
        // Serves files from ../web directory
        server.createContext("/", ex -> {

            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File("../web" + path);

            if (file.exists() && !file.isDirectory()) {
                byte[] content = Files.readAllBytes(file.toPath());

                if (path.endsWith(".html"))
                    ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                else if (path.endsWith(".css"))
                    ex.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
                else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                    ex.getResponseHeaders().set("Content-Type", "image/jpeg");
                else if (path.endsWith(".png"))
                    ex.getResponseHeaders().set("Content-Type", "image/png");
                else if (path.endsWith(".js"))
                    ex.getResponseHeaders().set("Content-Type", "application/javascript; charset=UTF-8");
                else if (path.endsWith(".json"))
                    ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

                ex.sendResponseHeaders(200, content.length);
                ex.getResponseBody().write(content);
            } else {
                ex.sendResponseHeaders(404, 0);
            }

            ex.getResponseBody().close();
        });

        // ===============================
        // 4️⃣ Form Handling Endpoints
        // ===============================
        // Handles Register, Login, Reset Password requests
        server.createContext("/register", ex -> handleForm(ex, "reg"));
        server.createContext("/login", ex -> handleForm(ex, "login"));
        server.createContext("/reset", ex -> handleForm(ex, "reset"));

        // ===============================
        // 5️⃣ User Profile API (JSON Response)
        // ===============================
        // Returns logged-in user data as JSON
        server.createContext("/api/userprofile", ex -> {

            DatabaseManager db = new DatabaseManager();
            Map<String, String> profile = db.getUserProfile(loggedInUser);

            // Convert user profile map to JSON format
            String json = String.format(
                "{\"fullName\":\"%s\",\"age\":\"%s\",\"weight\":\"%s\",\"week\":\"%s\",\"prevChild\":\"%s\",\"deliveryType\":\"%s\",\"medicalIssue\":\"%s\"}",
                profile.getOrDefault("fullName", "N/A"),
                profile.getOrDefault("age", "0"),
                profile.getOrDefault("weight", "0"),
                profile.getOrDefault("week", "0"),
                profile.getOrDefault("prevChild", "N/A"),
                profile.getOrDefault("deliveryType", "N/A"),
                profile.getOrDefault("medicalIssue", "N/A")
            );

            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] response = json.getBytes("UTF-8");

            ex.sendResponseHeaders(200, response.length);
            ex.getResponseBody().write(response);
            ex.getResponseBody().close();
        });

        // Start server
        PregnancyAssistant.loadDietData();
        server.createContext("/api/ask", new PregnancyAssistant.AskHandler());
        server.createContext("/api/upload", new PregnancyAssistant.UploadHandler());
        server.createContext("/api/history", new PregnancyAssistant.HistoryHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("MatriDisha Server started at: http://localhost:8081");
    }

    // ===============================
    // 6️⃣ Form Processing Method
    // ===============================
    // Handles Registration, Login & Password Reset logic
    private static void handleForm(HttpExchange ex, String type) throws IOException {

        // Allow only POST requests
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) return;

        // ===============================
        // 6.1️⃣ Read Form Data
        // ===============================
        String data = "";

        try (Scanner s = new Scanner(ex.getRequestBody()).useDelimiter("\\A")) {
            data = s.hasNext() ? s.next() : "";
        }

        // Convert form data into key-value map
        Map<String, String> p = new HashMap<>();
        for (String pair : data.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length > 1) {
                p.put(URLDecoder.decode(kv[0], "UTF-8"),
                      URLDecoder.decode(kv[1], "UTF-8"));
            }
        }

        DatabaseManager db = new DatabaseManager();
        boolean success = false;

        // ===============================
        // 6.2️⃣ Perform Action Based on Type
        // ===============================
        try {

            if (type.equals("reg")) {
                // Registration
                success = db.registerUser(
                        p.get("name"),
                        Integer.parseInt(p.get("age")),
                        Double.parseDouble(p.get("weight")),
                        Integer.parseInt(p.get("weeks")),
                        p.get("prev_child"),
                        p.get("delivery_type"),
                        p.get("medical_issue"),
                        p.get("username"),
                        p.get("password")
                );

            } else if (type.equals("login")) {
                // Login Validation
                success = db.validateLogin(
                        p.get("username"),
                        p.get("password")
                );

                if (success) loggedInUser = p.get("username");

            } else if (type.equals("reset")) {
                // Password Reset
                success = db.resetPassword(
                        p.get("username"),
                        p.get("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===============================
        // 6.3️⃣ Redirect After Action
        // ===============================
        String redirect = success
                ? (type.equals("login") ? "/dashboard.html" : "/index.html?success")
                : "/index.html?error";

        ex.getResponseHeaders().set("Location", redirect);
        ex.sendResponseHeaders(302, -1);
        ex.getResponseBody().close();
    }
}
