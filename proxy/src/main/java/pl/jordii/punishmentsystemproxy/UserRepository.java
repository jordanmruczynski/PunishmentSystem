package pl.jordii.punishmentsystemproxy;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.jordii.punishmentsystemproxy.config.PunishmentLayout;
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
    private final PunishmentLayout banLayout;

    public UserRepository(MySQLUserService mySQLUserService, MySQLPunishmentService mySQLPunishmentService, ExecutorService executorService, PunishmentLayout banLayout) {
        this.userService = mySQLUserService;
        this.punishmentService = mySQLPunishmentService;
        this.executorService = executorService;
        this.banLayout = banLayout;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final UUID uuid = player.getUniqueId();
        final Punishment punishment = punishmentService.getMutedUser(uuid);
        if (event.isCommand() || event.isProxyCommand()) return;
        if (punishment != null) {
            if (punishment.getExpireDate().isBefore(LocalDateTime.now())) {
                executorService.submit(() -> {
                    punishmentService.delete(punishment);
                });
                punishmentService.setUserUnmuted(punishment);
            } else {
                event.setCancelled(true);
                event.setMessage("");
                player.sendMessage(banLayout.getMuteMessage(punishment));
            }
        }
    }

    @EventHandler
    public void onLeave(ServerDisconnectEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        final Punishment punishment = punishmentService.getMutedUser(uuid);
        punishmentService.setUserUnmuted(punishment);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        executorService.submit(() -> {
           final User user = userService.findById(uuid);

           if (user != null) {
               final Punishment ban = punishmentService.findByUserUuid(uuid, PunishmentType.BAN);
               final Punishment mute = punishmentService.findByUserUuid(uuid, PunishmentType.MUTE);

               if (ban != null) {
                   if (ban.getExpireDate().isBefore(LocalDateTime.now())) {
                       punishmentService.delete(ban);
                   } else {
                       event.getPlayer().disconnect(new TextComponent(banLayout.getBanMessage(ban)));
                   }
               }

               if (mute != null) {
                   if (mute.getExpireDate().isBefore(LocalDateTime.now())) {
                       punishmentService.delete(mute);
                   } else {
                       punishmentService.setUserMuted(mute);
                   }
               }
           } else {
               userService.save(new User(uuid, event.getPlayer().getName()));
           }

        });
    }
}
