package pl.jordii.punishmentsystemproxy.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

public class BanLayout {
    //private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String layout;
    private final Map<String, String> placeholders;

    public BanLayout(File file) {
        JsonObject json = null;
        try {
            json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.layout = json.get("layout").getAsString();
        this.placeholders = new Gson().fromJson(json.get("placeholders"), HashMap.class);
    }

    public String getBanMessage(Punishment punishment) {
        Map<String, String> values = new HashMap<>();
        values.put("reason", punishment.getReason());
        values.put("uuid", punishment.getPlayer().toString());
        values.put("createDate", punishment.getCreateDate().format(dateTimeFormatter));
        values.put("expireDate", punishment.getExpireDate().format(dateTimeFormatter));
        values.put("admin", punishment.getAdmin().toString());

        String message = layout.replace("&", "§");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
