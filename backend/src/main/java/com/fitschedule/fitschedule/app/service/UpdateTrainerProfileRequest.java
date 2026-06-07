package com.fitschedule.fitschedule.app.service;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTrainerProfileRequest {

    @Size(min = 1, max = 100)
    private String firstName;

    @Size(min = 1, max = 100)
    private String lastName;

    @Pattern(regexp = "^$|^\\+?[0-9 ()-]{6,30}$", message = "Phone number format is invalid")
    private String phone;

    @Size(max = 500)
    private String photoUrl;

    private String bio;

    @Size(max = 100)
    private String specialization;

    private String certifications;
}