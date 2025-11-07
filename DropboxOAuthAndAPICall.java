import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DropboxOAuthAndAPICall {

    // === STEP 1: Fill in your Dropbox app credentials ===
    private static final String CLIENT_ID = "<YOUR_CLIENT_ID>";
    private static final String CLIENT_SECRET = "<YOUR_CLIENT_SECRET>";
    private static final String REDIRECT_URI = "https://oauth.pstmn.io/v1/callback"; // same as app settings

    public static void main(String[] args) {
        try {
            // === STEP 2: Generate authorization URL ===
            String authUrl = "https://www.dropbox.com/oauth2/authorize"
                    + "?client_id=" + CLIENT_ID
                    + "&response_type=code"
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                    + "&token_access_type=offline"
                    + "&scope=" + URLEncoder.encode("account_info.read", "UTF-8");

            System.out.println("🔗 STEP 1: Open this URL in your browser and authorize the app:");
            System.out.println(authUrl);
            System.out.println("\nAfter allowing access, copy the 'code' from the URL and paste it below.");

            // === STEP 3: Read the authorization code ===
            System.out.print("\nEnter the authorization code: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String authorizationCode = reader.readLine().trim();

            // === STEP 4: Exchange code for access token ===
            System.out.println("\n🔑 Exchanging code for access token...");
            String tokenUrl = "https://api.dropboxapi.com/oauth2/token";
            String data = "code=" + URLEncoder.encode(authorizationCode, "UTF-8")
                    + "&grant_type=authorization_code"
                    + "&client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8")
                    + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");

            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            // Read the token response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) response.append(line.trim());
            }

            System.out.println("\n✅ Token Response: " + response);
            String accessToken = extractToken(response.toString());

            // === STEP 5: Call Dropbox API ===
            System.out.println("\n🌐 Calling Dropbox API with access token...");
            callDropboxAPI(accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Extracts access_token value from JSON manually (no external libs)
    private static String extractToken(String json) {
        int start = json.indexOf("\"access_token\":");
        if (start == -1) return null;
        int firstQuote = json.indexOf('"', start + 16);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static void callDropboxAPI(String token) {
        try {
            URL apiUrl = new URL("https://api.dropboxapi.com/2/users/get_current_account");
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write("{}".getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) response.append(line.trim());
            }

            System.out.println("\n📦 Dropbox API Response:");
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
