package com.playconnect.repository;

import com.playconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Extending JpaRepository<User, Long> gives us, for free, with zero
 * implementation code:
 *   save(user)          -> INSERT or UPDATE
 *   findById(id)         -> SELECT by primary key
 *   findAll()            -> SELECT *
 *   deleteById(id)        -> DELETE by primary key
 *   count(), existsById(), ...and more
 *
 * Spring generates the actual implementation at startup via a proxy —
 * this interface just declares the contract.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom finder — Spring Data JPA parses this method NAME and
    // generates "SELECT * FROM users WHERE email = ?" automatically.
    // No SQL, no implementation needed.
    Optional<User> findByEmail(String email);

    // Used during registration (Day 39) to check for duplicate emails
    // before attempting an insert.
    boolean existsByEmail(String email);
}
