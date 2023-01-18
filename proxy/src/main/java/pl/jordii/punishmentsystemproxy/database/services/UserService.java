package pl.jordii.punishmentsystemproxy.database.services;

import pl.jordii.punishmentsystemproxy.database.model.User;

import java.util.UUID;

public interface UserService extends CrudService<User, UUID> {

    User findByName(String name);
}
