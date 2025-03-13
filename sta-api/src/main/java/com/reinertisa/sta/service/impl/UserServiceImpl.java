package com.reinertisa.sta.service.impl;

import com.reinertisa.sta.cache.CacheStore;
import com.reinertisa.sta.domain.RequestContext;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.entity.ConfirmationEntity;
import com.reinertisa.sta.entity.CredentialEntity;
import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.entity.UserEntity;
import com.reinertisa.sta.enumaration.Authority;
import com.reinertisa.sta.enumaration.EventType;
import com.reinertisa.sta.enumaration.LoginType;
import com.reinertisa.sta.event.UserEvent;
import com.reinertisa.sta.exception.ApiException;
import com.reinertisa.sta.repository.ConfirmationRepository;
import com.reinertisa.sta.repository.CredentialRepository;
import com.reinertisa.sta.repository.RoleRepository;
import com.reinertisa.sta.repository.UserRepository;
import com.reinertisa.sta.service.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.reinertisa.sta.utils.UserUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final BCryptPasswordEncoder encoder;
    private final CacheStore<String, Integer> userCache;
    private final ApplicationEventPublisher publisher;


    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, encoder.encode(password));
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        Optional<RoleEntity> role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LoginType.LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        var userEntity = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var credentialById = credentialRepository.getCredentialByUserEntityId(userId);
        return credentialById.orElseThrow(() -> new ApiException("Unable to find user credentials"));
    }

    @Override
    public User setUpMfa(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        String codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User cancelMfa(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeImageUri(StringUtils.EMPTY);
        userEntity.setQrCodeSecret(StringUtils.EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));

    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void resetPassword(String email) {
        UserEntity user = getUserEntityByEmail(email);
        ConfirmationEntity confirmation = getUserConfirmation(user);
        if (confirmation != null) {
            // send existing confirmation
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmation.getKey())));
        } else {
            ConfirmationEntity confirmationEntity = new ConfirmationEntity(user);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmationEntity.getKey())));
        }
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR code. Please try again.");
        }
    }

    private UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        return confirmationRepository.findByUserEntity(user).orElse(null);
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        RoleEntity role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }


}
