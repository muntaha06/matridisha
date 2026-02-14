import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class MainApp {
    private static String loggedInUser = ""; // [cite: 167]

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0); // [cite: 169]

        // 1. Static File Server [cite: 170-176]
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            File file = new File("../web" + path);
            if (file.exists()) {
                byte[] content = Files.readAllBytes(file.toPath());
                ex.sendResponseHeaders(200, content.length);
                ex.getResponseBody().write(content);
            } else {
                ex.sendResponseHeaders(404, 0);
            }
            ex.getResponseBody().close();
        });

        // 2. Form Handlers [cite: 180-187]
        server.createContext("/register", ex -> handleForm(ex, "reg"));
        server.createContext("/login", ex -> handleForm(ex, "login"));
        server.createContext("/reset", ex -> handleForm(ex, "reset"));

        // 3. User Profile JSON API [cite: 188-203]
        server.createContext("/api/userprofile", ex -> {
            DatabaseManager db = new DatabaseManager();
            Map<String, String> profile = db.getUserProfile(loggedInUser);
            
            // Fixed JSON Formatting 
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

        server.setExecutor(null);
        server.start();
        System.out.println("MatriDisha Server started at: http://localhost:8081"); // [cite: 213]
    }

    private static void handleForm(HttpExchange ex, String type) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) return;
        
        String data = "";
        // try-with-resources automatically closes the Scanner 's' 
        try (Scanner s = new Scanner(ex.getRequestBody()).useDelimiter("\\A")) {
            data = s.hasNext() ? s.next() : "";
        }

        Map<String, String> p = new HashMap<>();
        for (String pair : data.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length > 1) {
                p.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
            }
        }

        DatabaseManager db = new DatabaseManager();
        boolean success = false;

        try {
            if (type.equals("reg")) {
                success = db.registerUser(p.get("name"), Integer.parseInt(p.get("age")), 
                          Double.parseDouble(p.get("weight")), Integer.parseInt(p.get("weeks")), 
                          p.get("prev_child"), p.get("delivery_type"), p.get("medical_issue"), 
                          p.get("username"), p.get("password")); // [cite: 232-237]
            } else if (type.equals("login")) {
                success = db.validateLogin(p.get("username"), p.get("password")); // [cite: 239]
                if (success) loggedInUser = p.get("username");
            } else if (type.equals("reset")) {
                success = db.resetPassword(p.get("username"), p.get("password")); // [cite: 241]
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirect Logic [cite: 245-255]
        String redirect = success ? (type.equals("login") ? "/dashboard.html" : "/index.html?success") : "/index.html?error";
        ex.getResponseHeaders().set("Location", redirect);
        ex.sendResponseHeaders(302, -1);
        ex.getResponseBody().close();
    }
}