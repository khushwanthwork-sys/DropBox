import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DropboxOAuthAndAPICall {
    private static final String CLIENT_ID = "<app_id>";
    private static final String CLIENT_SECRET = "<app_secret>";
    private static final String REDIRECT_URI = "https://localhost/finish";

    public static void main(String[] args) {
        try {
            // Step 1: Print authorization link
            String authUrl = "https://www.dropbox.com/oauth2/authorize"
                    + "?client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8")
                    + "&response_type=code"
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                    + "&token_access_type=offline"
                    + "&scope=" + URLEncoder.encode("team_info.read team_data.member members.read account_info.read events.read", "UTF-8");

            System.out.println("Open this URL in your browser, approve access, and copy the 'code':");
            System.out.println(authUrl);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("\nEnter the authorization code here: ");
            String code = reader.readLine().trim();

            // Step 2: Prepare token exchange request
            String tokenUrl = "https://api.dropboxapi.com/oauth2/token";
            String params = "code=" + URLEncoder.encode(code, "UTF-8")
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
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            String response = readAll(conn);
            System.out.println("\nHTTP " + status + " Response:\n" + response);

            if (status != 200) {
                System.out.println("\n❌ Token exchange failed — check if redirect URI matches and code is fresh.");
                return;
            }

            String token = extractValue(response, "access_token");
            if (token == null) {
                System.out.println("❌ Could not extract access_token. Response:\n" + response);
                return;
            }

            // Step 3: Use token to call Dropbox API
            System.out.println("\n✅ Access token obtained. Calling Dropbox API...\n");
            callDropboxAPI(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readAll(HttpURLConnection conn) throws IOException {
        InputStream stream = (conn.getResponseCode() >= 400)
                ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) return "";
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private static String extractValue(String json, String key) {
        int i = json.indexOf("\"" + key + "\":");
        if (i == -1) return null;
        int start = json.indexOf('"', i + key.length() + 3);
        int end = json.indexOf('"', start + 1);
        return json.substring(start + 1, end);
    }

    private static void callDropboxAPI(String token) throws IOException {
        URL url = new URL("https://api.dropboxapi.com/2/team/get_info");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(false);

        String resp = readAll(conn);
        System.out.println("Dropbox API Response (" + conn.getResponseCode() + "):\n" + resp);
    }
}
