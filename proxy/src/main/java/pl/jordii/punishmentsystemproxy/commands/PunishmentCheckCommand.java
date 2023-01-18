package pl.jordii.punishmentsystemproxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.jordii.punishmentsystemproxy.config.PunishmentLayout;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.MySQLUserService;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.model.User;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PunishmentCheckCommand extends Command {

    private final MySQLPunishmentService punishmentService;
    private final MySQLUserService userService;
    private final ExecutorService executorService;
    private final PunishmentLayout banLayout;
    public PunishmentCheckCommand(MySQLPunishmentService punishmentService, MySQLUserService userService, ExecutorService executorService, PunishmentLayout banLayout) {
        super("punishmentcheck", "punishmentsystem.command.punishmentcheck", "check");
        this.punishmentService = punishmentService;
        this.userService = userService;
        this.executorService = executorService;
        this.banLayout = banLayout;
    }

    /**
     * punishment <ban/mute> <nick> <time> <reason..>
     **/

    @Override
    public void execute(CommandSender sender, String[] args) {
        final String playerName = args[0];

        executorService.submit(() -> {
            final User user = userService.findByName(playerName);
            if (user != null) {
                final Punishment ban = punishmentService.findByUserUuid(user.getUuid(), PunishmentType.BAN);
                final Punishment mute = punishmentService.findByUserUuid(user.getUuid(), PunishmentType.MUTE);
                sender.sendMessage(new TextComponent("----- BAN -----"));
                if (ban == null) {
                    sender.sendMessage(new TextComponent("Brak danych."));
                } else {
                    sender.sendMessage(new TextComponent("Wykryto rekord w bazie [NAJEDZ]"));
                }
                sender.sendMessage(new TextComponent("----- MUTE -----"));
                if (mute == null) {
                    sender.sendMessage(new TextComponent("Brak danych."));
                } else {
                    sender.sendMessage(new TextComponent("Wykryto rekord w bazie [NAJEDZ]"));
                }
            }
        });
    }

}
