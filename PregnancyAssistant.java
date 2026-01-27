import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class PregnancyAssistant {
    // Please verify this API KEY. If it is invalid, the app will fail.
    private static final String GEMINI_API_KEY = "AIzaSyAhPSD5RkCKLWJYlgCwUFM1Dy08lh3cCuI";
    private static final String USER_NAME = "Mukta Akter";
    private static final String GEMINI_MODEL = "gemini-2.5-flash";
    private static JSONObject dietData;

    public static void main(String[] args) throws Exception {
        loadDietData();

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new MainPageHandler());
        server.createContext("/api/ask", new AskHandler());
        server.createContext("/api/upload", new UploadHandler());
        server.createContext("/api/history", new HistoryHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on http://localhost:8081");
        System.out.println("Using Model: " + GEMINI_MODEL);
    }

    private static void loadDietData() {
        try {
            File f = new File("first_trimester_diet.json");
            if (f.exists()) {
                String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                dietData = new JSONObject(content);
            } else {
                dietData = new JSONObject();
            }
        } catch (Exception e) {
            dietData = new JSONObject();
        }
    }

    // --- HANDLERS ---

    static class MainPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getHTMLPage();
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class AskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                try {
                    System.out.println("Received Ask Request: " + body); // LOGGING
                    JSONObject json = new JSONObject(body);
                    String question = json.optString("question", "");
                    String sessionId = json.optString("sessionId", "");

                    if (sessionId.isEmpty())
                        sessionId = UUID.randomUUID().toString();

                    StringBuilder promptBuilder = new StringBuilder();
                    promptBuilder.append("You are a friendly, compassionate pregnancy assistant for ").append(USER_NAME)
                            .append(". ");
                    promptBuilder.append(
                            "Do NOT start with a greeting unless context requires it. Just answer the question directly. ");
                    promptBuilder.append("Make it sound like a real person talking to a pregnant woman (use 'আপু'). ");
                    promptBuilder.append("Split your answer into short, readable paragraphs. ");
                    promptBuilder.append("VERY IMPORTANT: You MUST highlight important keywords. ");
                    promptBuilder.append(
                            "Wrap key terms (like food names, medical terms, symptoms, advice) in triple asterisks like this: ***Apple*** or ***Headache***. ");
                    promptBuilder.append("I will color these blue in the app. Use this FREQUENTLY for visual appeal. ");
                    String foundInfo = searchInJSON(question);
                    if (!foundInfo.isEmpty()) {
                        System.out.println("Found info in JSON: " + foundInfo); // LOGGING
                        promptBuilder.append("Relevant Information from Knowledge Base: ").append(foundInfo)
                                .append("\n");
                        promptBuilder.append("INSTRUCTION: PRIORITIZE the information from the Knowledge Base above. ");
                        promptBuilder.append("If the answer is in the Knowledge Base, use it as the primary source. ");
                        promptBuilder.append(
                                "Only use outside knowledge if the Knowledge Base is incomplete or silent on the specific detail. ");
                    }

                    promptBuilder.append("User Question: ").append(question).append("\n");
                    promptBuilder.append("Language: Bengali. ");
                    promptBuilder.append(
                            "Do NOT use any markdown lists or bold * characters other than the triple asterisks.");

                    String answer = callGeminiAPI(promptBuilder.toString(), null);

                    saveToSession(sessionId, question, answer, null);

                    JSONObject response = new JSONObject();
                    response.put("answer", answer);
                    response.put("sessionId", sessionId);

                    sendJSON(exchange, response.toString());
                } catch (Exception e) {
                    e.printStackTrace(); // PRINT STACK TRACE
                    System.err.println("Error in AskHandler: " + e.getMessage());
                    sendJSON(exchange, new JSONObject()
                            .put("answer", "দুঃখিত, সমস্যা হয়েছে। (Error: " + e.getMessage() + ")").toString());
                }
            }
        }
    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                try {
                    System.out.println("Received Upload Request"); // LOGGING
                    JSONObject json = new JSONObject(body);
                    String base64Data = json.getString("image");
                    if (base64Data.contains(","))
                        base64Data = base64Data.split(",")[1];

                    String question = json.optString("question", "");
                    String sessionId = json.optString("sessionId", "");
                    if (sessionId.isEmpty())
                        sessionId = UUID.randomUUID().toString();

                    StringBuilder promptBuilder = new StringBuilder();
                    promptBuilder.append("You are a friendly, compassionate pregnancy assistant for ").append(USER_NAME)
                            .append(". ");
                    promptBuilder.append("Do NOT start with a greeting. Analyze this image. ");
                    if (question.isEmpty()) {
                        promptBuilder.append("Describe what is in the image and any pregnancy-related advice.");
                    } else {
                        promptBuilder.append("Answer this specific question about the image: ").append(question);
                    }
                    promptBuilder
                            .append("\nMake it sound like a real person talking to a pregnant woman (use 'আপু'). ");
                    promptBuilder.append("Split your answer into paragraphs. ");
                    promptBuilder.append("VERY IMPORTANT: You MUST highlight important keywords. ");
                    promptBuilder.append(
                            "Wrap key terms (like food names, medical terms, symptoms) in triple asterisks like this: ***Apple***. ");
                    promptBuilder.append("I will color these blue in the app. Use this FREQUENTLY. ");
                    promptBuilder.append("Language: Bengali. ");
                    promptBuilder.append(
                            "Do NOT use any markdown lists or bold * characters other than the triple asterisks.");

                    String answer = callGeminiAPI(promptBuilder.toString(), base64Data);

                    saveToSession(sessionId, question.isEmpty() ? "[Image Uploaded]" : question, answer,
                            json.getString("image"));

                    JSONObject response = new JSONObject();
                    response.put("answer", answer);
                    response.put("sessionId", sessionId);

                    sendJSON(exchange, response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error in UploadHandler: " + e.getMessage());
                    sendJSON(exchange, new JSONObject()
                            .put("answer", "দুঃখিত, সমস্যা হয়েছে। (Error: " + e.getMessage() + ")").toString());
                }
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            JSONArray historyData = loadHistoryFull();

            if (query != null && query.contains("id=")) {
                String id = query.split("id=")[1];
                JSONObject session = null;
                for (int i = 0; i < historyData.length(); i++) {
                    JSONObject s = historyData.getJSONObject(i);
                    if (s.getString("id").equals(id)) {
                        session = s;
                        break;
                    }
                }
                String resp = (session != null) ? session.getJSONArray("messages").toString() : "[]";
                sendJSON(exchange, resp);
            } else {
                JSONArray summaries = new JSONArray();
                for (int i = 0; i < historyData.length(); i++) {
                    JSONObject s = historyData.getJSONObject(i);
                    // Filter by current USER_NAME.
                    // If 'user' field is missing (old data), default it to "Anika".
                    String storedUser = s.optString("user", "Anika");
                    if (storedUser.equals(USER_NAME)) {
                        JSONObject sum = new JSONObject();
                        sum.put("id", s.getString("id"));
                        sum.put("title", s.optString("title", "Conversation"));
                        sum.put("timestamp", s.optString("timestamp", ""));
                        summaries.put(sum);
                    }
                }
                sendJSON(exchange, summaries.toString());
            }
        }
    }

    // --- HELPERS ---

    private static String readBody(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void sendJSON(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    private static JSONArray loadHistoryFull() {
        try {
            File f = new File("history.json");
            if (!f.exists())
                return new JSONArray();
            return new JSONArray(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static void saveToSession(String sessionId, String userMsg, String botMsg, String imageBase64) {
        try {
            JSONArray allSessions = loadHistoryFull();
            JSONObject targetSession = null;

            for (int i = 0; i < allSessions.length(); i++) {
                if (allSessions.getJSONObject(i).getString("id").equals(sessionId)) {
                    targetSession = allSessions.getJSONObject(i);
                    break;
                }
            }

            if (targetSession == null) {
                targetSession = new JSONObject();
                targetSession.put("id", sessionId);
                targetSession.put("timestamp", new Date().toString());
                String title = userMsg.length() > 25 ? userMsg.substring(0, 25) + "..." : userMsg;
                targetSession.put("title", title.isEmpty() ? "New Conversation" : title);
                targetSession.put("messages", new JSONArray());
                allSessions.put(targetSession);
            }

            // Always update the user to the current user (e.g. if switching users on same
            // browser)
            targetSession.put("user", USER_NAME);

            JSONObject msgRaw = new JSONObject();
            msgRaw.put("user", userMsg);
            msgRaw.put("bot", botMsg);
            if (imageBase64 != null)
                msgRaw.put("image", imageBase64);
            msgRaw.put("time", new Date().toString());

            targetSession.getJSONArray("messages").put(msgRaw);

            Files.write(Paths.get("history.json"), allSessions.toString(2).getBytes(StandardCharsets.UTF_8));

            // Save to SQL Database (Real-time)
            DatabaseManager.insertMessage(
                    sessionId,
                    USER_NAME,
                    userMsg,
                    botMsg,
                    new Date().toString(),
                    imageBase64);

        } catch (Exception e) {
            System.err.println("Save error: " + e);
        }
    }

    private static String callGeminiAPI(String prompt, String base64Image) throws Exception {
        String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL
                + ":generateContent?key=" + GEMINI_API_KEY;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject partText = new JSONObject().put("text", prompt);
        JSONArray parts = new JSONArray().put(partText);

        if (base64Image != null) {
            JSONObject partImage = new JSONObject()
                    .put("inline_data", new JSONObject()
                            .put("mime_type", "image/jpeg")
                            .put("data", base64Image));
            parts.put(partImage);
        }

        JSONObject content = new JSONObject().put("parts", parts);
        JSONObject payload = new JSONObject().put("contents", new JSONArray().put(content));

        OutputStream os = conn.getOutputStream();
        os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
        os.close();

        if (conn.getResponseCode() != 200) {
            // Read Error Stream
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder err = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                err.append(line);
            br.close();
            System.err.println("API Error Response: " + err.toString());
            throw new Exception("API Error " + conn.getResponseCode() + ": " + err.toString());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder resp = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            resp.append(line);
        br.close();

        return new JSONObject(resp.toString())
                .getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
    }

    private static String getHTMLPage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"bn\">\n");
        sb.append("<head>\n");
        sb.append("    <meta charset=\"UTF-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("    <title>Pregnancy Assistant</title>\n");
        sb.append("    <style>\n");
        sb.append(
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #fdf2f8; margin: 0; padding: 0; }\n");
        sb.append(
                "        .main-wrapper { max-width: 800px; margin: 20px auto; background: white; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); overflow: hidden; height: 90vh; display: flex; flex-direction: column; }\n");
        sb.append(
                "        .header { background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%); padding: 20px; color: white; display: flex; justify-content: space-between; align-items: center; }\n");
        sb.append("        .header h1 { margin: 0; font-size: 1.5em; }\n");
        sb.append(
                "        .btn-history { background: rgba(255,255,255,0.3); border: none; padding: 8px 15px; border-radius: 20px; color: white; cursor: pointer; font-weight: bold; }\n");
        sb.append(
                "        .chat-area { flex: 1; overflow-y: auto; padding: 20px; background: #fff; scroll-behavior: smooth; }\n");
        sb.append("        .message { margin-bottom: 20px; display: flex; flex-direction: column; max-width: 85%; }\n");
        sb.append("        .message.user { align-self: flex-end; align-items: flex-end; }\n");
        sb.append("        .message.bot { align-self: flex-start; align-items: flex-start; }\n");
        sb.append(
                "        .bubble { padding: 15px; border-radius: 15px; line-height: 1.6; word-wrap: break-word; font-size: 1.05em; }\n");
        sb.append(
                "        .user .bubble { background: #FF9A9E; color: white; border-bottom-right-radius: 2px; text-align: left; }\n");
        sb.append(
                "        .bot .bubble { background: #f0f2f5; color: #333; border-bottom-left-radius: 2px; border: 1px solid #e1e4e8; }\n");
        sb.append(
                "        .input-area { padding: 20px; background: #f9f9f9; border-top: 1px solid #eee; display: flex; gap: 10px; flex-direction: column; }\n");
        sb.append("        .input-row { display: flex; gap: 10px; }\n");
        sb.append(
                "        .input-box { flex: 1; padding: 12px; border: 1px solid #ddd; border-radius: 25px; outline: none; font-size: 1em; }\n");
        sb.append(
                "        .btn-send { background: #FF9A9E; border: none; padding: 10px 25px; border-radius: 25px; color: white; cursor: pointer; font-weight: bold; }\n");
        sb.append(
                "        .img-preview { max-width: 200px; border-radius: 10px; margin-bottom: 8px; display: block; border: 2px solid #FF9A9E; }\n");
        sb.append("        .topic-blue { color: #2196F3; font-weight: 700; font-size: 1.1em; }\n");
        sb.append(
                "        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999; }\n");
        sb.append(
                "        .modal { background: white; width: 90%; max-width: 400px; margin: 50px auto; border-radius: 15px; padding: 20px; max-height: 80vh; overflow-y: auto; position: relative; }\n");
        sb.append(
                "        .session-item { padding: 15px; border-bottom: 1px solid #eee; cursor: pointer; transition: background 0.2s; }\n");
        sb.append("        .session-item:hover { background: #fff0f5; }\n");
        sb.append(
                "        .close-btn { position: absolute; right: 15px; top: 15px; cursor: pointer; font-size: 24px; color: #666; font-weight: bold; width: 30px; height: 30px; line-height: 30px; text-align: center; }\n");
        sb.append("        .close-btn:hover { color: #000; background: #eee; border-radius: 50%; }\n");
        sb.append(
                "        .new-chat-btn { width: 100%; padding: 12px; background: #667eea; color: white; border: none; border-radius: 10px; margin-bottom: 15px; cursor: pointer; font-weight: bold; }\n");
        sb.append("    </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("    <div class=\"main-wrapper\">\n");
        sb.append("        <div class=\"header\">\n");
        sb.append("            <h1>🤰 Pregnancy Assistant</h1>\n");
        sb.append("            <button class=\"btn-history\" onclick=\"openHistory()\">📜 History</button>\n");
        sb.append("        </div>\n");
        sb.append("        <div class=\"chat-area\" id=\"chatBox\">\n");
        sb.append("            <div class=\"message bot\"><div class=\"bubble\">আসসালামু আলাইকুম " + USER_NAME
                + " আপু, আমি তোমাকে কীভাবে সাহায্য করতে পারি?</div></div>\n");
        sb.append("        </div>\n");
        sb.append("        <div class=\"input-area\">\n");
        sb.append("            <div id=\"previewArea\"></div>\n");
        sb.append("            <div class=\"input-row\">\n");
        sb.append(
                "                <button class=\"btn-send\" onclick=\"document.getElementById('fileIn').click()\">📷</button>\n");
        sb.append(
                "                <input type=\"file\" id=\"fileIn\" hidden accept=\"image/*\" onchange=\"handleFile(this)\">\n");
        sb.append(
                "                <input type=\"text\" class=\"input-box\" id=\"msgIn\" placeholder=\"Type here...\" onkeypress=\"if(event.key==='Enter') send()\">\n");
        sb.append("                <button class=\"btn-send\" id=\"sendBtn\" onclick=\"send()\">➤</button>\n");
        sb.append("            </div>\n");
        sb.append("        </div>\n");
        sb.append("    </div>\n");
        sb.append("    <div class=\"modal-overlay\" id=\"modalLayer\">\n");
        sb.append("        <div class=\"modal\">\n");
        sb.append("            <div class=\"close-btn\" onclick=\"closeHistory()\">&times;</div>\n");
        sb.append("            <h2 style=\"margin-top:0\">Conversations</h2>\n");
        sb.append(
                "            <button class=\"new-chat-btn\" onclick=\"startNewChat()\">+ New Conversation</button>\n");
        sb.append("            <div id=\"sessionList\">Loading...</div>\n");
        sb.append("        </div>\n");
        sb.append("    </div>\n");
        sb.append("    <script>\n");
        sb.append("        let currentSessionId = localStorage.getItem('preg_sess_id') || '';\n");
        sb.append("        let currentImageBase64 = null;\n");
        sb.append("        function handleFile(input) {\n");
        sb.append("            if(input.files && input.files[0]) {\n");
        sb.append("                let reader = new FileReader();\n");
        sb.append("                reader.onload = function(e) {\n");
        sb.append("                    currentImageBase64 = e.target.result;\n");
        sb.append(
                "                    document.getElementById('previewArea').innerHTML = '<img src=\"'+currentImageBase64+'\" class=\"img-preview\">';\n");
        sb.append("                };\n");
        sb.append("                reader.readAsDataURL(input.files[0]);\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        async function send() {\n");
        sb.append("            let txt = document.getElementById('msgIn').value.trim();\n");
        sb.append("            if(!txt && !currentImageBase64) return;\n");
        sb.append("            addBubble(txt, 'user', currentImageBase64, false);\n");
        sb.append("            let payload = { question: txt, sessionId: currentSessionId };\n");
        sb.append("            let url = '/api/ask';\n");
        sb.append("            if(currentImageBase64) {\n");
        sb.append("                payload.image = currentImageBase64;\n");
        sb.append("                url = '/api/upload';\n");
        sb.append("            }\n");
        sb.append("            document.getElementById('msgIn').value = '';\n");
        sb.append("            document.getElementById('previewArea').innerHTML = '';\n");
        sb.append("            let savedImg = currentImageBase64;\n");
        sb.append("            currentImageBase64 = null;\n");
        sb.append("            document.getElementById('fileIn').value = '';\n");
        sb.append("            document.getElementById('sendBtn').disabled = true;\n");
        sb.append("            let loadId = addBubble('Thinking...', 'bot', null, false);\n");
        sb.append("            try {\n");
        sb.append("                let res = await fetch(url, {\n");
        sb.append("                    method: 'POST',\n");
        sb.append("                    body: JSON.stringify(payload)\n");
        sb.append("                });\n");
        sb.append("                let data = await res.json();\n");
        sb.append("                currentSessionId = data.sessionId;\n");
        sb.append("                localStorage.setItem('preg_sess_id', currentSessionId);\n");
        sb.append("                document.getElementById(loadId).remove();\n");
        sb.append("                addBubble(data.answer, 'bot', null, true);\n");
        sb.append("            } catch(e) {\n");
        sb.append("                document.getElementById(loadId).innerText = 'Error: ' + e;\n");
        sb.append("            }\n");
        sb.append("            document.getElementById('sendBtn').disabled = false;\n");
        sb.append("        }\n");
        sb.append("        function formatResponse(text) {\n");
        sb.append("            if(!text) return '';\n");
        sb.append(
                "            let pro = text.replace(/\\*\\*\\*(.*?)\\*\\*\\*/g, '<span class=\"topic-blue\">$1</span>');\n");
        sb.append("            pro = pro.replace(/\\*/g, '');\n");
        sb.append("            pro = pro.replace(/\\n/g, '<br>');\n");
        sb.append("            return pro;\n");
        sb.append("        }\n");
        sb.append("        function addBubble(html, sender, imgData, doTyping) {\n");
        sb.append("            let div = document.createElement('div');\n");
        sb.append("            div.className = 'message ' + sender;\n");
        sb.append("            div.id = 'msg-' + Date.now() + Math.random();\n");
        sb.append("            let imgHtml = imgData ? '<img src=\"'+imgData+'\" class=\"img-preview\"><br>' : '';\n");
        sb.append("            let contentHtml = '';\n");
        sb.append("            if(doTyping && sender === 'bot') {\n");
        sb.append("                contentHtml = '<span class=\"typing-content\"></span>';\n");
        sb.append("            } else {\n");
        sb.append("                contentHtml = html;\n");
        sb.append("            }\n");
        sb.append("            div.innerHTML = '<div class=\"bubble\">' + imgHtml + contentHtml + '</div>';\n");
        sb.append("            document.getElementById('chatBox').appendChild(div);\n");
        sb.append("            document.getElementById('chatBox').scrollTop = 100000;\n");
        sb.append("            if(doTyping && sender === 'bot') {\n");
        sb.append("                typeWriter(html, div.querySelector('.typing-content'));\n");
        sb.append("            }\n");
        sb.append("            return div.id;\n");
        sb.append("        }\n");
        sb.append("        async function typeWriter(text, el) {\n");
        sb.append("            let parts = text.split(/(\\*{3}.*?\\*{3})/g);\n");
        sb.append("            for(let part of parts) {\n");
        sb.append("                if(!part) continue;\n");
        sb.append("                let isBold = part.startsWith('***') && part.endsWith('***');\n");
        sb.append("                let content = isBold ? part.substring(3, part.length-3) : part;\n");
        sb.append("                let targetSpan = null;\n");
        sb.append("                if(isBold) {\n");
        sb.append("                    targetSpan = document.createElement('span');\n");
        sb.append("                    targetSpan.className = 'topic-blue';\n");
        sb.append("                    el.appendChild(targetSpan);\n");
        sb.append("                } else {\n");
        sb.append("                    targetSpan = el;\n");
        sb.append("                }\n");
        sb.append("                await typeText(content, targetSpan);\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        function typeText(str, el) {\n");
        sb.append("            return new Promise(resolve => {\n");
        sb.append("                let i = 0;\n");
        sb.append("                function type() {\n");
        sb.append("                    if(i < str.length) {\n");
        sb.append("                        if(str[i] === '\\n') el.innerHTML += '<br>';\n");
        sb.append("                        else el.innerHTML += str[i];\n");
        sb.append("                        i++;\n");
        sb.append("                        document.getElementById('chatBox').scrollTop = 100000;\n");
        sb.append("                        setTimeout(type, 20);\n");
        sb.append("                    } else {\n");
        sb.append("                        resolve();\n");
        sb.append("                    }\n");
        sb.append("                }\n");
        sb.append("                type();\n");
        sb.append("            });\n");
        sb.append("        }\n");
        sb.append("        async function openHistory() {\n");
        sb.append("            document.getElementById('modalLayer').style.display = 'block';\n");
        sb.append("            let res = await fetch('/api/history');\n");
        sb.append("            let list = await res.json();\n");
        sb.append("            let html = '';\n");
        sb.append("            list.reverse().forEach(s => {\n");
        sb.append("                html += `<div class=\"session-item\" onclick=\"loadSession('${s.id}')\">\n");
        sb.append("                            <div><b>${s.title}</b></div>\n");
        sb.append("                            <div class=\"session-date\">${s.timestamp}</div>\n");
        sb.append("                         </div>`;\n");
        sb.append("            });\n");
        sb.append("            document.getElementById('sessionList').innerHTML = html || 'No history.';\n");
        sb.append("        }\n");
        sb.append("        function closeHistory() {\n");
        sb.append("            document.getElementById('modalLayer').style.display = 'none';\n");
        sb.append("        }\n");
        sb.append("        function startNewChat() {\n");
        sb.append("            currentSessionId = '';\n");
        sb.append("            localStorage.removeItem('preg_sess_id');\n");
        sb.append(
                "            document.getElementById('chatBox').innerHTML = '<div class=\"message bot\"><div class=\"bubble\">আসসালামু আলাইকুম "
                        + USER_NAME + " আপু, আমি তোমাকে কীভাবে সাহায্য করতে পারি?</div></div>';\n");
        sb.append("            closeHistory();\n");
        sb.append("        }\n");
        sb.append("        async function loadSession(id) {\n");
        sb.append("            currentSessionId = id;\n");
        sb.append("            localStorage.setItem('preg_sess_id', id);\n");
        sb.append("            let res = await fetch('/api/history?id=' + id);\n");
        sb.append("            let msgs = await res.json();\n");
        sb.append("            document.getElementById('chatBox').innerHTML = '';\n");
        sb.append("            msgs.forEach(m => {\n");
        sb.append("                let img = m.image || null;\n");
        sb.append("                if(m.user || img) addBubble(m.user, 'user', img, false);\n");
        sb.append("                if(m.bot) addBubble(formatResponse(m.bot), 'bot', null, false);\n");
        sb.append("            });\n");
        sb.append("            closeHistory();\n");
        sb.append("        }\n");
        sb.append("    </script>\n");
        sb.append("</body>\n");
        sb.append("</html>");

        return sb.toString();
    }

    private static String searchInJSON(String question) {
        StringBuilder result = new StringBuilder();
        if (dietData == null)
            return "";

        String[] keywords = question.split("\\s+");

        // Search in Food List (খাদ্য_তালিকা)
        JSONObject foodList = dietData.optJSONObject("খাদ্য_তালিকা");
        if (foodList != null) {
            for (String category : foodList.keySet()) {
                JSONArray items = foodList.optJSONArray(category);
                if (items != null) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String name = item.optString("নাম", "");
                        // Check if item name is in question OR question contains item name
                        boolean match = false;
                        if (question.contains(name))
                            match = true;

                        if (match) {
                            result.append("Food: ").append(name).append(" - ");
                            JSONArray reasons = item.optJSONArray("বিস্তারিত_কারণ");
                            if (reasons != null) {
                                for (int k = 0; k < reasons.length(); k++)
                                    result.append(reasons.getString(k)).append(" ");
                            }
                            result.append("\n");
                        }
                    }
                }
            }
        }

        // Search in Exercises (প্রতিদিনের_ব্যায়াম)
        JSONArray exercises = dietData.optJSONArray("প্রতিদিনের_ব্যায়াম");
        if (exercises != null) {
            for (int i = 0; i < exercises.length(); i++) {
                JSONObject ex = exercises.getJSONObject(i);
                String name = ex.optString("ব্যায়ামের_নাম", "");
                if (question.contains(name) || name.contains(question)) { // blurred match
                    result.append("Exercise: ").append(name).append(" - ");
                    JSONArray reasons = ex.optJSONArray("বিস্তারিত_কারণ");
                    if (reasons != null) {
                        for (int k = 0; k < reasons.length(); k++)
                            result.append(reasons.getString(k)).append(" ");
                    }
                    result.append("\n");
                }
            }
        }

        // Search in Problems (প্রথম_ত্রৈমাসিকের_সাধারণ_সমস্যা_ও_সমাধান)
        JSONArray problems = dietData.optJSONArray("প্রথম_ত্রৈমাসিকের_সাধারণ_সমস্যা_ও_সমাধান");
        if (problems != null) {
            for (int i = 0; i < problems.length(); i++) {
                JSONObject prob = problems.getJSONObject(i);
                String probName = prob.optString("সমস্যা", "");
                // Simple keyword check
                boolean match = false;
                if (question.contains(probName))
                    match = true;
                for (String word : keywords) {
                    if (word.length() > 3 && probName.contains(word))
                        match = true;
                }

                if (match) {
                    result.append("Problem: ").append(probName).append(" - Solution: ");
                    JSONArray sols = prob.optJSONArray("সমাধান");
                    if (sols != null) {
                        for (int k = 0; k < sols.length(); k++)
                            result.append(sols.getString(k)).append(" ");
                    }
                    result.append("\n");
                }
            }
        }

        // Search in Cautions (সতর্কতা)
        JSONArray cautions = dietData.optJSONArray("সতর্কতা");
        if (cautions != null) {
            for (int i = 0; i < cautions.length(); i++) {
                String c = cautions.getString(i);
                for (String word : keywords) {
                    if (word.length() > 3 && c.contains(word)) { // If any significant word matches
                        result.append("Caution: ").append(c).append("\n");
                        break;
                    }
                }
            }
        }

        return result.toString();
    }
}
