package pl.jordii.punishmentsystemproxy;

import net.md_5.bungee.api.plugin.Plugin;
import pl.jordii.punishmentsystemproxy.commands.PunishmentCommand;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.MySQLUserService;
import pl.jordii.punishmentsystemproxy.mysql.MySQLConnection;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PunishmentSystem extends Plugin {

    private MySQLConnection connection;
    private MySQLUserService userService;
    private MySQLPunishmentService punishmentService;
    private ExecutorService executorService;

    @Override
    public void onEnable() {
        connection = new MySQLConnection("mysqlCredentials.json");
        userService = new MySQLUserService(connection);
        punishmentService = new MySQLPunishmentService(connection);
        executorService = Executors.newCachedThreadPool();

        try {
            userService.createTable();
            punishmentService.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerListener(this, new UserRepository(userService, punishmentService, executorService));
        getProxy().getPluginManager().registerCommand(this, new PunishmentCommand(punishmentService, executorService));

    }

    @Override
    public void onDisable() {
        connection.closeConnection();
    }
}
