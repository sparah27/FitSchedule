package com.fitschedule.backend.fitschedule.app.dto.response;

import com.fitschedule.backend.fitschedule.app.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String photoUrl;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .photoUrl(user.getPhotoUrl())
                .role(user.getClass().getSimpleName().toUpperCase())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}