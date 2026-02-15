import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class MainApp {
    public static void main(String[] args) throws IOException {
        // ৮০৮৩ পোর্টে সার্ভার তৈরি
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
        
        // স্ট্যাটিক ফাইল হ্যান্ডলার (HTML, CSS, Images লোড করার জন্য)
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            
            // রুট পাথে থাকলে index.html এ পাঠিয়ে দিবে
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // ফাইলের লোকেশন (web ফোল্ডারের ভেতর)
            File file = new File("web" + path);
            
            if (file.exists() && !file.isDirectory()) {
                byte[] content = Files.readAllBytes(file.toPath());
                
                // ফাইলের এক্সটেনশন অনুযায়ী Content-Type সেট করা (ঐচ্ছিক কিন্তু ভালো)
                if (path.endsWith(".html")) ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                 if (path.endsWith(".jpg") || path.endsWith(".jpeg")) ex.getResponseHeaders().set("Content-Type", "image/jpeg");
                if (path.endsWith(".png")) ex.getResponseHeaders().set("Content-Type", "image/png");
                if (path.endsWith(".css")) ex.getResponseHeaders().set("Content-Type", "text/css");

                ex.sendResponseHeaders(200, content.length);
                OutputStream os = ex.getResponseBody();
                os.write(content);
                os.close();
            } else {
                // ফাইল না পাওয়া গেলে ৪MD৪ এরর
                String response = "404 (Not Found): ফাইলটি খুঁজে পাওয়া যায়নি।";
                ex.sendResponseHeaders(404, response.length());
                OutputStream os = ex.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.setExecutor(null); 
        server.start();
        System.out.println("Server started successfully!");
        System.out.println("Visit: http://localhost:8083/diseases.html");
    }
}