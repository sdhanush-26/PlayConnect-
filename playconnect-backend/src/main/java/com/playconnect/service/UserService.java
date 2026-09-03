package com.playconnect.service;

import com.playconnect.entity.User;
import com.playconnect.exception.PlayerNotFoundException;
import com.playconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Existing generic create — still used by non-auth flows (e.g. any
    // remaining test/admin paths). Stores whatever password string it's
    // given as-is; register() below is the properly-hashed path and is
    // what /api/auth/register actually calls starting today.
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "A user with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    // Proper registration path — hashes the password with BCrypt before
    // ever touching the database. From today onward, this is what real
    // signups go through; createUser() above stays only for internal/
    // legacy callers that pass an already-hashed value.
    public User register(String name, String email, String rawPassword,
                          String phone, Double latitude, Double longitude) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A user with email " + email + " already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // never store rawPassword directly
        user.setPhone(phone);
        user.setLatitude(latitude);
        user.setLongitude(longitude);

        return userRepository.save(user);
    }

    // Verifies email/password against the stored hash. passwordEncoder
    // .matches() is the only correct way to do this comparison — BCrypt
    // hashes can't be reversed, so this hashes the incoming raw password
    // using the same salt embedded in the stored hash and compares the
    // results, rather than ever decoding anything.
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.playconnect.exception.InvalidCredentialsException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            // Deliberately the same error message as "user not found" above —
            // revealing which one failed would let an attacker enumerate
            // valid email addresses by trying logins and reading the error.
            throw new com.playconnect.exception.InvalidCredentialsException(
                    "Invalid email or password");
        }

        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = getUser(id);

        existing.setName(updatedUser.getName());
        existing.setPhone(updatedUser.getPhone());
        existing.setLatitude(updatedUser.getLatitude());
        existing.setLongitude(updatedUser.getLongitude());

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}