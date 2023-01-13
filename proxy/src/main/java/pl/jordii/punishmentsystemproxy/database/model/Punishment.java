package pl.jordii.punishmentsystemproxy.database.model;

import pl.jordii.punishmentsystemproxy.database.services.PunishmentType;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Punishment {

    private Long id;
    private UUID player;
    private String reason;
    private UUID admin;
    private PunishmentType punishmentType;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
    private String ip_address;

    public Punishment(Long id, UUID player, String reason, UUID admin, PunishmentType punishmentType, LocalDateTime createDate, LocalDateTime expireDate, String ip_address) {
        this.id = id;
        this.player = player;
        this.reason = reason;
        this.admin = admin;
        this.punishmentType = punishmentType;
        this.createDate = createDate;
        this.expireDate = expireDate;
        this.ip_address = ip_address;
    }

    public Punishment(UUID player, String reason, UUID admin, PunishmentType punishmentType, LocalDateTime createDate, LocalDateTime expireDate, String ip_address) {
        this.player = player;
        this.reason = reason;
        this.admin = admin;
        this.punishmentType = punishmentType;
        this.createDate = createDate;
        this.expireDate = expireDate;
        this.ip_address = ip_address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getAdmin() {
        return admin;
    }

    public void setAdmin(UUID admin) {
        this.admin = admin;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public void setPunishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
}
