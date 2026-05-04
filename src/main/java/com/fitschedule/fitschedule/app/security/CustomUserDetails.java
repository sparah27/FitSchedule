package com.fitschedule.fitschedule.app.security;

import com.fitschedule.fitschedule.app.model.entity.Client;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        if (user instanceof Trainer) {
            role = "ROLE_TRAINER";
        } else if (user instanceof Client) {
            role = "ROLE_CLIENT";
        } else {
            role = "ROLE_USER";
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // We use email as the username
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getActive());
    }

    public Long getUserId() {
        return user.getId();
    }
}