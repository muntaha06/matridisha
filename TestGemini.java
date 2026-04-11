import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestGemini {
    public static void main(String[] args) {
        String apiKey = "AIzaSyAhIEqv6by-EaBYQEBnFAmKjty0YJT3PnU";
        String[] models = {"gemini-1.5-flash", "gemini-1.5-flash-latest", "gemini-1.5-pro", "gemini-pro"};
        String[] versions = {"v1", "v1beta"};
        
        for (String version : versions) {
            for (String model : models) {
                try {
                    String urlStr = "https://generativelanguage.googleapis.com/" + version + "/models?key=" + apiKey;
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) sb.append(line);
                                java.nio.file.Files.write(java.nio.file.Paths.get("models.json"), sb.toString().getBytes(StandardCharsets.UTF_8));
                                System.out.println("SUCCESS. Dumped to models.json");
                        }
                    } else {
                        InputStream errorStream = conn.getErrorStream();
                        if (errorStream != null) {
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) sb.append(line);
                                System.out.println("FAILED:  " + version + " - " + model + " (Code: " + code + ") - " + sb.toString());
                            }
                        } else {
                            System.out.println("FAILED:  " + version + " - " + model + " (Code: " + code + ")");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR:   " + version + " - " + model + " - " + e.getMessage());
                }
            }
        }
    }
}
