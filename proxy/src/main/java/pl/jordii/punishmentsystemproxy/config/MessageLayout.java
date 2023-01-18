package pl.jordii.punishmentsystemproxy.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageLayout {
    //private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String layout;
    private final Map<String, String> placeholders;

    public MessageLayout(File file) {
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
        values.put("uuid", replaceUUIDtoNick(punishment.getPlayer()));
        values.put("createDate", punishment.getCreateDate().format(dateTimeFormatter));
        values.put("expireDate", punishment.getExpireDate().format(dateTimeFormatter));
        values.put("admin", replaceUUIDtoNick(punishment.getAdmin()));

        String message = layout.replace("&", "§");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public TextComponent getMuteMessage(Punishment punishment) {
        final String layout = "§cZostałeś globalnie wyciszony! §e[NAJEDZ]";
        final TextComponent component = new TextComponent(layout);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cPowód: " + punishment.getReason() + "\n§cAdministrator: " + replaceUUIDtoNick(punishment.getAdmin()) + "\n§6OD §7" + getDateFormatted(punishment.getCreateDate()) + "\n§6DO §7" + getDateFormatted(punishment.getExpireDate()) + "\n§cTwój nick: " + replaceUUIDtoNick(punishment.getPlayer()))));
        return component;
    }

    public String getDateFormatted(LocalDateTime date) {
        return date.format(dateTimeFormatter);
    }

    private String replaceUUIDtoNick(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid).getName();
    }
}
