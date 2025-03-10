package com.reinertisa.sta.utils;

import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.entity.CredentialEntity;
import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.entity.UserEntity;
import org.springframework.beans.BeanUtils;


import java.time.LocalDateTime;
import java.util.UUID;

import static com.reinertisa.sta.constant.Constants.NINETY_DAYS;
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

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }

    private static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now());
    }
}
