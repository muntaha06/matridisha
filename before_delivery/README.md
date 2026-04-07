# প্রসবপূর্ব নির্দেশনা App

## Setup Instructions

### 1. Create a placeholder icon (optional)
Create an `icon.png` file in the project root, or the app will work without it.

### 2. Create a background image (optional)
Create a `background.jpg` file in the project root for the background. If not available, the gradient will be used.

### 3. Change Username
Edit `home.html` and change the username in this line:
```html
<span class="username">X</span>
```
Replace `X` with the desired username.

## Running the Application

### Compile the Java Server
```bash
javac SimpleHttpServer.java
```

### Run the Server
```bash
java SimpleHttpServer
```

### Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

The server will automatically serve `home.html` when you access the root URL.

## Files Structure
- `home.html` - Main home page
- `home.css` - Stylesheet for the home page
- `SimpleHttpServer.java` - Simple HTTP server (plain Java, no servlets)
- `icon.png` - App icon (optional)
- `background.jpg` - Background image (optional)

## Features
- Beautiful responsive design
- Welcome message with customizable username
- Dropdown to select trimester (0-3, 4-6, 7-9 months)
- Three main panels: Khabar (Food), Bayam (Exercise), Sotorkota (Precautions)
- Click handlers ready for future implementation

