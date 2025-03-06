package com.reinertisa.sta.utils;

import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.entity.UserEntity;


import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class UserUtils {
    public static UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
       return UserEntity.builder()
               .userId(UUID.randomUUID().toString())
               .firstName(firstName)
               .lastName(lastName)
               .email(email)
               .lastLogin(LocalDateTime.now())
               .accountNonExpired(true)
               .accountNonLocked(true)
               .mfa(false)
               .enabled(false)
               .loginAttempts(0)
               .qrCodeSecret(EMPTY)
               .phone(EMPTY)
               .bio(EMPTY)
               .imageUrl("https://cdn-icon-png.flaticon/512/149/149071.png")
               .role(role)
               .build();
    }
}
