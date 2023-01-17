package pl.jordii.punishmentsystemproxy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.jordii.punishmentsystemproxy.config.BanLayout;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.MySQLUserService;
import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.model.User;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class UserRepository implements Listener {

    private final MySQLUserService userService;
    private final MySQLPunishmentService punishmentService;
    private final ExecutorService executorService;
    private final BanLayout banLayout;

    public UserRepository(MySQLUserService mySQLUserService, MySQLPunishmentService mySQLPunishmentService, ExecutorService executorService, BanLayout banLayout) {
        this.userService = mySQLUserService;
        this.punishmentService = mySQLPunishmentService;
        this.executorService = executorService;
        this.banLayout = banLayout;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent event) {
        event.getPlayer().sendMessage(new TextComponent("takeeeweweweeweewewwewewe"));
        UUID uuid = event.getPlayer().getUniqueId();
        System.out.printf("DEBUG JOIN");
        executorService.submit(() -> {
           final User user = userService.findById(uuid);
           if (user != null) {
               final Punishment ban = punishmentService.findByUserUuid(uuid, PunishmentType.BAN);
               System.out.printf(ban.getPlayer().toString());
               if (ban != null) {
                   if (ban.getExpireDate().isBefore(LocalDateTime.now())) {
                       punishmentService.delete(ban);
                   } else {
                       event.getPlayer().disconnect(new TextComponent(banLayout.getBanMessage(ban)));
                   }
                   System.out.printf("DEBUG 2");
               }
           } else {
               userService.save(new User(uuid, event.getPlayer().getName()));
           }
        });
    }
}
