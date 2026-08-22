package com.playconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Shape of the JSON body clients send when creating or updating a user.
 * @Valid on the controller method (see UserController) triggers these
 * checks automatically — invalid requests get rejected with a 400 before
 * any code in the service layer even runs.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phone;

    private Double latitude;

    private Double longitude;
}
