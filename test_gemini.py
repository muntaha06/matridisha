import urllib.request
import json

API_KEY = "AIzaSyAhPSD5RkCKLWJYlgCwUFM1Dy08lh3cCuI"

models_to_test = [
    "gemini-1.5-flash",
    "gemini-1.5-flash-latest",
    "gemini-1.5-pro",
    "gemini-pro"
]

versions = ["v1", "v1beta"]

for version in versions:
    for model in models_to_test:
        url = f"https://generativelanguage.googleapis.com/{version}/models/{model}:generateContent?key={API_KEY}"
        data = {
            "contents": [{"parts": [{"text": "hello"}]}]
        }
        
        req = urllib.request.Request(url, data=json.dumps(data).encode("utf-8"), headers={"Content-Type": "application/json"})
        try:
            with urllib.request.urlopen(req) as response:
                print(f"SUCCESS: {version} - {model}")
        except urllib.error.HTTPError as e:
            error_body = e.read().decode('utf-8')
            print(f"FAILED:  {version} - {model} (Code: {e.code}) - {error_body}")
        except Exception as e:
            print(f"ERROR:   {version} - {model} - {str(e)}")
