package pl.jordii.punishmentsystemproxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.jordii.punishmentsystemproxy.config.PunishmentLayout;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;
import pl.jordii.punishmentsystemproxy.utils.TimeParser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PunishmentCommand extends Command {

    private final MySQLPunishmentService punishmentService;
    private final ExecutorService executorService;
    private final PunishmentLayout banLayout;
    public PunishmentCommand(MySQLPunishmentService punishmentService, ExecutorService executorService, PunishmentLayout banLayout) {
        super("punishment", "punishmentsystem.command.punishment", "ban", "mute");
        this.punishmentService = punishmentService;
        this.executorService = executorService;
        this.banLayout = banLayout;
    }

    /**
     * punishment <ban/mute> <nick> <time> <reason..>
     **/

    @Override
    public void execute(CommandSender sender, String[] args) {
        final PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());
        final String playerName = args[1];
        final String time = args[2];
        final String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        final UUID adminUuid = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : null;

//        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
//        if (player == null) {
//            sender.sendMessage(new TextComponent("Player not found"));
//            return;
//        }

        final Duration duration = TimeParser.parseTime(time);

        final Punishment punishment = new Punishment(player.getUniqueId(), reason, adminUuid, type, LocalDateTime.now(), LocalDateTime.now().plus(duration), player.getAddress().getAddress().getHostAddress().toString());
        executorService.submit(() -> {
            punishmentService.save(punishment);
        });

        switch (type) {
            case BAN:
                player.disconnect(new TextComponent(banLayout.getBanMessage(punishment)));
                break;
            case MUTE:
                punishmentService.setUserMuted(punishment);
                player.sendMessage(new TextComponent("§cZostales wyciszony!"));
                break;
            default:
                break;
        }

    }
}
