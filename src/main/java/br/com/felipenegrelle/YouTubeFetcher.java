package br.com.felipenegrelle;

import br.com.laider.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class YouTubeFetcher {

    public static String getLatestVideoUrl(String apiKey, String channelId) {
        try {
            String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="
                    + channelId + "&order=date&type=video&maxResults=5&key=" + apiKey;

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            Scanner scanner = new Scanner(url.openStream());

            StringBuilder response = new StringBuilder();

            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }

            scanner.close();

            Gson gson = new Gson();

            JsonObject json = gson.fromJson(response.toString(), JsonObject.class);

            JsonArray items = json.getAsJsonArray("items");

            if (!items.isEmpty()) {
                String expectedTitle = generateExpectedTitle();

                for (int i = 0; i < items.size(); i++) {
                    JsonObject video = items.get(i).getAsJsonObject();
                    JsonObject snippet = video.getAsJsonObject("snippet");
                    String videoTitle = snippet.get("title").getAsString();

                    if (videoTitle.toLowerCase().contains(expectedTitle.toLowerCase())) {
                        String videoId = video.getAsJsonObject("id").get("videoId").getAsString();
                        return "https://www.youtube.com/watch?v=" + videoId;
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        return null;
    }

    private static String generateExpectedTitle() {
        LocalDate today = LocalDate.now();

        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

        return "Homilia Diária | " + dayOfWeek;
    }

    public static String downloadAudio(String videoUrl, String basePath) {
        String outputPath = basePath + "homilia-" + new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        String command = "yt-dlp -x --audio-format mp3 -o " + outputPath + " " + videoUrl;

        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();

            String finalPath = outputPath + ".mp3";

            File file = new File(finalPath);

            if (file.exists() && file.isFile()) {
                System.out.println("Áudio baixado com sucesso: " + finalPath);
                return finalPath;
            } else {
                System.err.println("Erro: O áudio não foi baixado corretamente. Verifique o yt-dlp.");
                return null;
            }
        } catch (IOException | InterruptedException e) {
            Logger.error(e);

            return null;
        }
    }
}
