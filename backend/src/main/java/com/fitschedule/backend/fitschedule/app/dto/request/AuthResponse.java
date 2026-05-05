package com.fitschedule.backend.fitschedule.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;  // "CLIENT" or "TRAINER"
}