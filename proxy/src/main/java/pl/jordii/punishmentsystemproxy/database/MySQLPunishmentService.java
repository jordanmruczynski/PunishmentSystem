package pl.jordii.punishmentsystemproxy.database;

import pl.jordii.punishmentsystemproxy.database.model.Punishment;
import pl.jordii.punishmentsystemproxy.database.model.User;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentService;
import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;
import pl.jordii.punishmentsystemproxy.mysql.MySQLConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MySQLPunishmentService implements PunishmentService {

    private final MySQLConnection connection;

    public MySQLPunishmentService(MySQLConnection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS punishments (id BIGINT AUTO_INCREMENT PRIMARY KEY, player_uuid VARCHAR(36) NOT NULL, reason VARCHAR(255) NOT NULL, admin VARCHAR(36), punishmentType ENUM('BAN', 'MUTE') NOT NULL, createDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, expireDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, ip_address VARCHAR(36) NOT NULL, FOREIGN KEY (player_uuid) REFERENCES users(uuid))";
        try (Connection conn = connection.getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
    @Override
    public Set<Punishment> findAll() {
        final Set<Punishment> punishments = new HashSet<>();
        final String sql = "SELECT * FROM punishments";
        try (Connection conn = connection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                String reason = rs.getString("reason");
                UUID admin = UUID.fromString(rs.getString("admin"));
                PunishmentType punishmentType = PunishmentType.valueOf(rs.getString("punishmentType"));
                LocalDateTime createDate = rs.getTimestamp("createDate").toLocalDateTime();
                LocalDateTime expireDate = rs.getTimestamp("expireDate").toLocalDateTime();
                String ip_address = rs.getString("ip_address");
                long id = rs.getLong("id");
                punishments.add(new Punishment(id, uuid, reason, admin, punishmentType, createDate, expireDate, ip_address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return punishments;
    }

    @Override
    public Punishment findById(Long id) {
        final String sql = "SELECT * FROM punishments WHERE id = ?";
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    String reason = rs.getString("reason");
                    UUID admin = UUID.fromString(rs.getString("admin"));
                    PunishmentType punishmentType = PunishmentType.valueOf(rs.getString("punishmentType"));
                    LocalDateTime createDate = rs.getTimestamp("createDate").toLocalDateTime();
                    LocalDateTime expireDate = rs.getTimestamp("expireDate").toLocalDateTime();
                    String ip_address = rs.getString("ip_address");
                    return new Punishment(id, uuid, reason, admin, punishmentType, createDate, expireDate, ip_address);
                }
            };
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public Punishment save(Punishment punishment) {
        final String sql = "INSERT INTO punishments (player_uuid, reason, admin, punishmentType, createDate, expireDate, ip_address) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, punishment.getPlayer().toString());
            stmt.setString(2, punishment.getReason());
            stmt.setString(3, punishment.getAdmin().toString());
            stmt.setString(4, punishment.getPunishmentType().name());
            stmt.setTimestamp(5, Timestamp.valueOf(punishment.getCreateDate()));
            stmt.setTimestamp(6, Timestamp.valueOf(punishment.getExpireDate()));
            stmt.setString(7, punishment.getIp_address());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return punishment;
    }

    @Override
    public void delete(Punishment punishment) {
        final String sql = "DELETE FROM punishments WHERE id = ?";
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, punishment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM punishments WHERE id = ?";
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Punishment findByUserUuid(UUID uuid, PunishmentType type) {
        final String sql = "SELECT * FROM punishments WHERE player_uuid = ? AND punishmentType = ?";
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Punishment(
                            rs.getLong("id"),
                            UUID.fromString(rs.getString("player_uuid")),
                            rs.getString("reason"),
                            UUID.fromString(rs.getString("admin")),
                            PunishmentType.valueOf(rs.getString("punishmentType")),
                            rs.getTimestamp("createDate").toLocalDateTime(),
                            rs.getTimestamp("expireDate").toLocalDateTime(),
                            rs.getString("ip_address")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
