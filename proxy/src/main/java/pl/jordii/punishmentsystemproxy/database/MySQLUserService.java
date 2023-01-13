package pl.jordii.punishmentsystemproxy.database;

import pl.jordii.punishmentsystemproxy.database.model.User;
import pl.jordii.punishmentsystemproxy.database.services.UserService;
import pl.jordii.punishmentsystemproxy.mysql.MySQLConnection;

import java.lang.invoke.StringConcatFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MySQLUserService implements UserService {

    private final MySQLConnection connection;

    public MySQLUserService(MySQLConnection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS users (uuid VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY, name VARCHAR(30) NOT NULL";
        try (Statement stmt = connection.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    @Override
    public Set<User> findAll()  {
        final Set<User>  users = new HashSet<>();
        final String sql = "SELECT * FROM users";
        try (Statement stmt = connection.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                users.add(new User(uuid, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }

    @Override
    public User findById(UUID uuid) {
        final String sql = "SELECT * FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new User(uuid, name);
                }
            };
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public User save(User user) {
        final String sql = "INSERT INTO users(uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUuid().toString());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    @Override
    public void delete(User user) {
        final String sql = "DELETE FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(UUID uuid) {
        final String sql = "DELETE FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
