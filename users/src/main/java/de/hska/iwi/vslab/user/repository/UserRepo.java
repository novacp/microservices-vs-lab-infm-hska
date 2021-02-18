package de.hska.iwi.vslab.user.repository;

import org.springframework.stereotype.Repository;

import de.hska.iwi.vslab.user.model.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, String> {
    User findByUsername(String username);
}
