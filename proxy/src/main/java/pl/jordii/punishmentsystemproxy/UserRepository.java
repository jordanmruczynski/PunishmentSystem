package pl.jordii.punishmentsystemproxy;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.MySQLUserService;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class UserRepository implements Listener {

    private final MySQLUserService userService;
    private final MySQLPunishmentService punishmentService;
    private final ExecutorService executorService;

    public UserRepository(MySQLUserService mySQLUserService, MySQLPunishmentService mySQLPunishmentService, ExecutorService executorService) {
        this.userService = mySQLUserService;
        this.punishmentService = mySQLPunishmentService;
        this.executorService = executorService;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PunishmentType type = PunishmentType.BAN;
        executorService.submit(() -> {
            Punishment ban = punishmentService.findByUserUuid(uuid, type);
            if (ban != null) {
                if (ban.getExpireDate().isBefore(LocalDateTime.now())) {
                    punishmentService.delete(ban);
                } else {
                    event.getPlayer().disconnect(new TextComponent("zbanowanko xd"));
                }
            }
        });

    }
}
