package com.example.bankcards.converter;

import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.model.UserDto;

import java.util.Collection;
import java.util.List;

public class UserConverter {
    public static UserDto toDto(UserEntity entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                UserDto.RoleEnum.valueOf(entity.getRole().name())
        );
    }

    public static UserDetailsDto toUserDetailsDto(UserEntity entity) {
        return new UserDetailsDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                UserDto.RoleEnum.valueOf(entity.getRole().name())
        );
    }

    public static List<UserDto> toDtos(Collection<UserEntity> entities) {
        return entities.stream()
                .map(UserConverter::toDto)
                .toList();
    }

    public static UserEntity toEntity(UserDto dto) {
        return new UserEntity(
                dto.getId(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                UserRole.valueOf(dto.getRole().name())
        );
    }
}
