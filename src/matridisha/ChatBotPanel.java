package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * ChatBotPanel: Provides an offline AI assistant interface within the application.
 * It communicates with a local LLM (Large Language Model) via the Ollama API.
 */
public class ChatBotPanel extends JPanel {
    JTextArea chatArea;
    JTextField inputField;
    DashboardFrame parent;
    Color darkPink = new Color(199, 21, 133);

    public ChatBotPanel(DashboardFrame parent) {
        this.parent = parent;
        
        // --- Panel Configuration ---
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Header Section ---
        JLabel header = new JLabel("MatriDisha AI Assistant (Offline)", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(darkPink);
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // --- Chat Display Area ---
        // JTextArea is used to display the conversation history
        chatArea = new JTextArea();
        chatArea.setEditable(false);      // Users cannot type directly into the history
        chatArea.setLineWrap(true);       // Wrap text to the next line if it's too long
        chatArea.setWrapStyleWord(true);  // Wrap at word boundaries
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        
        // Wrap the chat area in a scroll pane to handle long conversations
        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        add(scroll, BorderLayout.CENTER);

        // --- Input Section (Bottom) ---
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton sendBtn = new JButton("Ask AI");
        sendBtn.setBackground(darkPink);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Arial", Font.BOLD, 14));
        sendBtn.setPreferredSize(new Dimension(100, 40));
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        // Send message when the button is clicked or Enter is pressed in the text field
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    /**
     * Handles the logic for sending a user query and receiving an AI response.
     */
    private void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) return; // Do nothing if input is empty

        // Display user's message in the chat area
        chatArea.append("You: " + userText + "\n");
        inputField.setText(""); // Clear the input field
        
        /**
         * MULTITHREADING: We run the API call in a new Thread.
         * This prevents the GUI from "freezing" or becoming unresponsive 
         * while waiting for the local AI to generate a response.
         */
        new Thread(() -> {
            String aiResponse = getLocalAIResponse(userText);
            
            // UI updates must be performed on the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> {
                chatArea.append("AI: " + aiResponse + "\n\n");
            });
        }).start();
    }

    /**
     * Logic to connect to the local Ollama API server.
     * Communicates via HTTP POST and manually parses the JSON response.
     */
    private String getLocalAIResponse(String prompt) {
        try {
            // Default endpoint for Ollama's local generation API
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            /**
             * JSON Payload:
             * model: Specifies the AI model (e.g., llama3).
             * prompt: The user's question.
             * stream: Set to false to get the full response at once.
             */
            String jsonInputString = "{\"model\": \"llama3\", \"prompt\": \"" + prompt + "\", \"stream\": false}";

            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the server response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                
                /**
                 * MANUAL JSON PARSING:
                 * Since no external JSON libraries are used, we extract the "response" field
                 * by finding the index of the keys and slicing the string.
                 */
                String result = response.toString();
                int start = result.indexOf("\"response\":\"") + 12;
                int end = result.indexOf("\",\"done\"");
                
                if (start > 11 && end > start) {
                    // Clean up the text: handle newline characters and escaped quotes
                    return result.substring(start, end)
                                 .replace("\\n", "\n")
                                 .replace("\\\"", "\"");
                }
                return "AI is thinking, but the response format is unexpected.";
            }
        } catch (Exception e) {
            // Error handling for connection issues (e.g., Ollama server is not running)
            return "Connection Error: Please ensure 'Ollama' is running locally on your PC.";
        }
    }
}