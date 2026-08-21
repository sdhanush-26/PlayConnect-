package com.playconnect.controller;

import com.playconnect.entity.User;
import com.playconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for User management. Every method here just delegates to
 * UserService — no business logic lives in the controller itself.
 * Test each endpoint with Postman:
 *
 *   POST   http://localhost:8080/api/users
 *   GET    http://localhost:8080/api/users
 *   GET    http://localhost:8080/api/users/1
 *   PUT    http://localhost:8080/api/users/1
 *   DELETE http://localhost:8080/api/users/1
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users — create a new user
    // Example JSON body:
    // {
    //   "name": "Dhanush",
    //   "email": "dhanush@example.com",
    //   "password": "placeholder-hash",
    //   "phone": "9999900001",
    //   "latitude": 14.68,
    //   "longitude": 77.60
    // }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/users — list everyone
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{id} — fetch one user
    // Throws PlayerNotFoundException if the id doesn't exist — this is
    // currently an unhandled 500 error until Day 14 wires up
    // @RestControllerAdvice for clean 404 responses instead.
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    // PUT /api/users/{id} — update name/phone/location
    // (email and password are intentionally not editable here — see
    // the comment in UserService.updateUser for why)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
