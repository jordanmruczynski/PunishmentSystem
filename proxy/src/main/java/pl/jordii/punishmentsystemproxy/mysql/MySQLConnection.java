package pl.jordii.punishmentsystemproxy.mysql;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private final HikariDataSource dataSource;
    private MySQLCredentials credentials;

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://";
    private static final String OPTIONS = "?autoreconnect=true&useSSL=false";

    public MySQLConnection(String credentialsFile) {
        Gson gson = new Gson();
        try {
            credentials = gson.fromJson(new FileReader(credentialsFile), MySQLCredentials.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase() + OPTIONS);
        config.setUsername(credentials.getUser());
        config.setPassword(credentials.getPassword());
        config.setDriverClassName(DRIVER);
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection() {
        dataSource.close();
    }
}
