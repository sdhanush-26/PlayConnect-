package com.playconnect.controller;

import com.playconnect.dto.ProfileResponse;
import com.playconnect.dto.UserRequest;
import com.playconnect.dto.UserResponse;
import com.playconnect.entity.User;
import com.playconnect.service.ProfileService;
import com.playconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Test in Postman:
 *   GET http://localhost:8080/api/profile/1
 *   PUT http://localhost:8080/api/profile/1  body: same shape as UserRequest
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    // GET /api/profile/{id} — the combined view: user info + sports +
    // (placeholder) match stats, all in one call.
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    // PUT /api/profile/{id} — updates the editable basic-info fields.
    // Reuses UserService.updateUser (from Day 11/13) rather than
    // duplicating that logic here; profile editing and user editing
    // are the same underlying operation.
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id,
                                                         @Valid @RequestBody UserRequest request) {
        User updatedData = new User();
        updatedData.setName(request.getName());
        updatedData.setPhone(request.getPhone());
        updatedData.setLatitude(request.getLatitude());
        updatedData.setLongitude(request.getLongitude());

        User updated = userService.updateUser(id, updatedData);

        UserResponse response = new UserResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getPhone(),
                updated.getLatitude(),
                updated.getLongitude(),
                updated.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }
}
