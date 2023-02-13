package pl.jordii.punishmentsystemproxy;

import net.md_5.bungee.api.plugin.Plugin;
import pl.jordii.punishmentsystemproxy.commands.PunishmentCommand;
import pl.jordii.punishmentsystemproxy.config.PunishmentLayout;
import pl.jordii.punishmentsystemproxy.database.MySQLPunishmentService;
import pl.jordii.punishmentsystemproxy.database.MySQLUserService;
import pl.jordii.punishmentsystemproxy.mysql.MySQLConnection;
import pl.jordii.punishmentsystemproxy.utils.FileCopy;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PunishmentSystem extends Plugin {

    private MySQLConnection connection;
    private MySQLUserService userService;
    private MySQLPunishmentService punishmentService;
    private ExecutorService executorService;
    private PunishmentLayout banLayout;
    private final String configFileName = "mysqlCredentials.json";
    private final String banLayoutFileName = "banDisconnectLayout.json";
    private File configFile;
    private File banLayoutFile;

    @Override
    public void onEnable() {
        setupFiles();
        connection = new MySQLConnection(getDataFolder() + "/" + configFileName);
        userService = new MySQLUserService(connection);
        punishmentService = new MySQLPunishmentService(connection);
        executorService = Executors.newCachedThreadPool();
        banLayout = new PunishmentLayout(banLayoutFile);

        try {
            userService.createTable();
            punishmentService.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerListener(this, new UserRepository(userService, punishmentService, executorService, banLayout));
        getProxy().getPluginManager().registerCommand(this, new PunishmentCommand(punishmentService, executorService, banLayout, userService));

    }

    @Override
    public void onDisable() {
        connection.closeConnection();
    }

    private void setupFiles() {
        File dataFolder = getDataFolder();
        configFile = new File(dataFolder, configFileName);
        banLayoutFile = new File(dataFolder, banLayoutFileName);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        FileCopy.createFileFromResource(configFileName, configFile, this);
        FileCopy.createFileFromResource(banLayoutFileName, banLayoutFile, this);
    }

}
