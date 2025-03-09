package br.com.felipenegrelle;

import br.com.laider.utils.Text;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class Properties implements Serializable {

    @SerializedName("sender_number")
    @Expose
    private String senderNumber;
    @SerializedName("youtube_api_key")
    @Expose
    private String youtubeApiKey;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("base_path")
    @Expose
    private String basePath;
    @SerializedName("base_path_test")
    @Expose
    private String basePathTest;
    @SerializedName("production")
    @Expose
    private Boolean production;
    @Serial
    private final static long serialVersionUID = 3851624006816561817L;

    public static Properties load(String jsonPath) throws IOException {
        if (Text.isNotEmpty(jsonPath) && Files.exists(Path.of(jsonPath))) {
            Gson gson = new Gson();

            String fileContent = Files.readString(Path.of(jsonPath));

            return gson.fromJson(fileContent, Properties.class);
        } else {
            return null;
        }
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }

    public void setYoutubeApiKey(String youtubeApiKey) {
        this.youtubeApiKey = youtubeApiKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePathTest() {
        return basePathTest;
    }

    public void setBasePathTest(String basePathTest) {
        this.basePathTest = basePathTest;
    }

    public Boolean isProduction() {
        return production;
    }

    public void setProduction(Boolean production) {
        this.production = production;
    }
}
