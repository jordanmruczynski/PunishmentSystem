package pl.jordii.punishmentsystemproxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PunishmentCommand extends Command {

    private final MySQLPunishmentService punishmentService;
    private final ExecutorService executorService;
    public PunishmentCommand(MySQLPunishmentService punishmentService, ExecutorService executorService) {
        super("punishment", "punishmentsystem.command.punishment", "ban", "mute");
        this.punishmentService = punishmentService;
        this.executorService = executorService;
    }

    /**
     * punishment <ban/mute> <nick> <time> <reason..>
     **/

    @Override
    public void execute(CommandSender sender, String[] args) {
        PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());
        String playerName = args[1];
        String time = args[2];
        String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        UUID adminUuid = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : null;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(new TextComponent("Player not found"));
            return;
        }

        Punishment punishment = new Punishment(player.getUniqueId(), reason, adminUuid, type, LocalDateTime.now(), parseTime(time), player.getAddress().getAddress().getHostAddress().toString());
        executorService.submit(() -> {
            punishmentService.save(punishment);
        });
        player.disconnect(new TextComponent("you have been banned"));

    }

//    private long parseTime(String time) {
//        long duration = -1;
//        try {
//            duration = Duration.parse(time.toLowerCase()).toMillis();
//        } catch (DateTimeParseException e) {
//            e.printStackTrace();
//        }
//        return duration;
//    }

    private LocalDateTime parseTime(String time) {
        LocalDateTime duration = LocalDateTime.MIN;
        try {
            duration = LocalDateTime.parse(time);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return duration;
    }
}
