package com.playconnect.service;

import com.playconnect.entity.User;
import com.playconnect.exception.PlayerNotFoundException;
import com.playconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic layer. Controllers (Day 12) will call these methods —
 * they never talk to UserRepository directly. This is also where rules
 * that don't belong in the database go (e.g. rejecting a duplicate email
 * with a clear message before Hibernate would throw a raw SQL exception).
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection — Spring automatically supplies UserRepository
    // here at startup. Preferred over @Autowired on a field because it
    // makes the dependency explicit and the class easier to unit test.
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "A user with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = getUser(id); // reuses the lookup + exception above

        existing.setName(updatedUser.getName());
        existing.setPhone(updatedUser.getPhone());
        existing.setLatitude(updatedUser.getLatitude());
        existing.setLongitude(updatedUser.getLongitude());
        // Email and password intentionally excluded here — those get
        // dedicated endpoints later (Day 39+) with their own validation
        // rather than being silently overwritten by a generic update.

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
