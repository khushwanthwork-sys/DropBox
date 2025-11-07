# Dropbox API OAuth2 Authentication + API Call

This project demonstrates OAuth 2.0 (Authorization Code Flow) authentication and an API call to the Dropbox API, built entirely in plain **Java (no external dependencies)**.

Developed as part of the **CloudEagle Product Management Internship – Dropbox (Business) API Assignment**.

---

## 🚀 Features
- Implements **OAuth2 Authorization Code Flow** manually using `HttpURLConnection`.
- Exchanges the authorization `code` for an `access_token`.
- Calls:
  - `team/get_info` → for Dropbox Business accounts, or  
  - `users/get_current_account` → for Personal accounts.
- Runs on **any online Java compiler** (e.g., OnlineGDB, JDoodle, Replit).

---

## 🧠 How It Works
1. The program prints an **authorization URL**.
2. Open it in your browser → log in to Dropbox → approve the app.
3. Copy the `code` from the redirected URL (after `?code=`).
4. Paste the code into the console.
5. The program exchanges the code for an access token and calls a Dropbox API endpoint.
6. The API response (JSON) is printed to the console.

---

## ⚙️ Setup
1. Go to [Dropbox Developer Console](https://www.dropbox.com/developers/apps).
2. Create a new **Scoped Access App**:
   - **Full Dropbox** (for personal) or **Dropbox Business API** (for team).
3. Add Redirect URI: https://localhost/finish
4. Copy:
- App key → `CLIENT_ID`
- App secret → `CLIENT_SECRET`
5. Paste these in the Java file:
```java
private static final String CLIENT_ID = "<YOUR_CLIENT_ID>";
private static final String CLIENT_SECRET = "<YOUR_CLIENT_SECRET>";
private static final String REDIRECT_URI = "https://localhost/finish";
```

##▶️ Run Instructions

Use OnlineGDB Java Compiler
 or any Java IDE.

Run the file DropboxOAuthAndAPICall_Final.java.

Follow console instructions to authorize and view API response.

🧾 Example Output
Open this URL in your browser:
https://www.dropbox.com/oauth2/authorize?client_id=abc123...

Enter the authorization code: a1b2c3d4e5

✅ Token Response:
{"access_token":"sl.BCDEFG123456","token_type":"bearer"}

📦 Dropbox API Response:
{"account_id":"dbid:AABBCC","name":{"display_name":"Khushwanth"},"email":"you@example.com"}
