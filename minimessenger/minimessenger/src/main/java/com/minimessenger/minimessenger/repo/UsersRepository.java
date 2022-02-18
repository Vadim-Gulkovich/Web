package com.minimessenger.minimessenger.repo;

import com.minimessenger.minimessenger.models.db.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<Users, Long> {

    @Query(value = "SELECT * FROM USERS WHERE email = ?1", nativeQuery = true)
    Users findByEmailAddress(String emailAddress);

}
