package com.reinertisa.sta.utils;

import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.entity.CredentialEntity;
import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.entity.UserEntity;
import com.reinertisa.sta.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import org.springframework.beans.BeanUtils;


import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.reinertisa.sta.constant.Constants.GET_ARRAYS_LLC;
import static com.reinertisa.sta.constant.Constants.NINETY_DAYS;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
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

    public static BiFunction<String, String, QrData> qrDataFunction = (email, qrCodeSecret) ->
            new QrData.Builder()
                    .issuer(GET_ARRAYS_LLC)
                    .label(email)
                    .secret(qrCodeSecret)
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(8)
                    .period(30)
                    .build();

    public static BiFunction<String, String, String> qrCodeImageUri = (email, qrCodeSecret) -> {
        QrData data = qrDataFunction.apply(email, qrCodeSecret);
        ZxingPngQrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (Exception exception) {
            throw new ApiException("Unable to create QR code URI");
        }

        return getDataUriForImage(imageData, generator.getImageMimeType());
    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}
