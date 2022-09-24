package org.spotify_backup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlaylistWriter {
    private final JProgressBar progressBar;
    private final Configure configure;
    private int counter;
    private int totalTracks;

    public PlaylistWriter(JProgressBar progressBar, Configure configure) {
        this.progressBar = progressBar;
        this.configure = configure;
    }

    public List<String[]> getTracks(JsonObject response) {
        JsonArray items;
        JsonElement tracksObj = response.get("tracks");

        if (tracksObj == null) {
            items = response.get("items").getAsJsonArray();
        } else {
            items = tracksObj.getAsJsonObject().get("items").getAsJsonArray();
        }

        List<String[]> entries = new ArrayList<>();

        for (JsonElement itemElement : items) {
            JsonObject item = itemElement.getAsJsonObject();
            JsonObject track = item.get("track").getAsJsonObject();

            List<String> artists = new ArrayList<>();
            track.get("artists").getAsJsonArray().forEach(artistElement -> {
                artists.add(artistElement.getAsJsonObject().get("name").getAsString());
            });

            String trackName = track.get("name").getAsString();
            String artist = String.join("::", artists);
            String albumName = track.get("album").getAsJsonObject().get("name").getAsString();
            String duration = track.get("duration_ms").getAsString();
            String addedAt = item.get("added_at").getAsString();
            entries.add(new String[]{trackName, artist, albumName, duration, addedAt});
        }
        return entries;
    }

    public void write()
            throws IOException, InterruptedException {
        PlaylistDownloader request = new PlaylistDownloader(configure);
        JsonObject playlist = request.getPlaylist();

        String playlistName = playlist.get("name").getAsString();
        JsonObject tracksObj = playlist.get("tracks").getAsJsonObject();
        String next = tracksObj.get("next").getAsString();
        this.totalTracks = tracksObj.get("total").getAsInt();

        List<String[]> tracks = getTracks(playlist);

        try (
                Writer writer = Files.newBufferedWriter(Paths.get(String.format("./%s.csv", playlistName)));
                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            String[] header = {"Name", "Artist(s)", "Album", "Duration", "Added At"};
            csvWriter.writeNext(header);
            step(tracks, csvWriter);

            while (true) {
                JsonObject response = request.getNextPage(next);
                tracks = getTracks(response);
                step(tracks, csvWriter);

                JsonElement nextObj = response.get("next");
                if (nextObj.isJsonNull()) {
                    break;
                }
                next = nextObj.getAsString();
            }
            JOptionPane.showMessageDialog(null, "Writing process completed."
                    , "Spotify Backup", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void step(List<String[]> tracks, CSVWriter writer) {
        for (String[] track : tracks) {
            writer.writeNext(track);
            counter++;
            int progress = (int) ((((double) counter) / totalTracks) * 100);
            progressBar.setValue(progress);
        }
    }
}
