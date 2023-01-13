package pl.jordii.punishmentsystemproxy.database.services;

import pl.jordii.punishmentsystemproxy.database.model.Punishment;

import java.util.UUID;

public interface PunishmentService extends CrudService<Punishment, Long> {

    Punishment findByUserUuid(UUID uuid, PunishmentType type);

}
