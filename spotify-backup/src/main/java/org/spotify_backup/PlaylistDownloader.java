package org.spotify_backup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;

public class PlaylistDownloader {
    String playlistID;
    String clientID;
    String clientSecret;
    private String accessToken;

    public PlaylistDownloader(Configure configure) {
        this.clientID = configure.getClientID();
        this.clientSecret = configure.getClientSecret();
        this.playlistID = configure.getPlaylistID();
    }

    public JsonObject getPlaylist()
            throws IOException, InterruptedException {
        String fieldsQuery = "?fields=name,tracks(total,next,items(added_at,track(name,duration_ms,artists.name,album.name)))";
        String URIString = String.format("https://api.spotify.com/v1/playlists/%s" + fieldsQuery, playlistID);

        this.accessToken = createAccessToken(clientID, clientSecret);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", String.format("Bearer %s", accessToken));

        String responseString = makeRequest(URIString, headers, null);
        return JsonParser.parseString(responseString).getAsJsonObject();
    }

    public JsonObject getNextPage(String nextPageURIString)
            throws IOException, InterruptedException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", String.format("Bearer %s", accessToken));

        String responseString = makeRequest(nextPageURIString, headers, null);
        return JsonParser.parseString(responseString).getAsJsonObject();
    }

    private String makeRequest(String URIString, HashMap<String, String> headers, String body)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(URIString));

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.forEach(builder::header);

        if (body != null) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String createAccessToken(String clientID, String clientSecret)
            throws IOException, InterruptedException {
        String token = String.format("%s:%s", clientID, clientSecret);
        token = Base64.getEncoder().encodeToString(token.getBytes());
        token = String.format("Basic %s", token);

        String requestBody = "grant_type=client_credentials";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        String responseString = makeRequest("https://accounts.spotify.com/api/token", headers, requestBody);

        JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
        return responseJson.get("access_token").getAsString();
    }
}
