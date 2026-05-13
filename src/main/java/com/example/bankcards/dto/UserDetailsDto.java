package com.example.bankcards.dto;

import com.example.bankcards.model.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsDto extends UserDto implements UserDetails {

    public UserDetailsDto(Long id, String username, String email, String password, RoleEnum role) {
        super(id, username, email, password, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
    }
}
