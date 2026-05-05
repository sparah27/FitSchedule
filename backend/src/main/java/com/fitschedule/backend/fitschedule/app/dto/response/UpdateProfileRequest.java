package com.fitschedule.backend.fitschedule.app.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Pattern(
            regexp = "^$|^\\+?[0-9 ()-]{6,30}$",
            message = "Phone number format is invalid"
    )
    private String phone;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;
}