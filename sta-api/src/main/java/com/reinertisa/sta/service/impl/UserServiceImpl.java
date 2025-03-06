package com.reinertisa.sta.service.impl;

import com.reinertisa.sta.entity.ConfirmationEntity;
import com.reinertisa.sta.entity.CredentialEntity;
import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.entity.UserEntity;
import com.reinertisa.sta.enumaration.Authority;
import com.reinertisa.sta.enumaration.EventType;
import com.reinertisa.sta.event.UserEvent;
import com.reinertisa.sta.exception.ApiException;
import com.reinertisa.sta.repository.ConfirmationRepository;
import com.reinertisa.sta.repository.CredentialRepository;
import com.reinertisa.sta.repository.RoleRepository;
import com.reinertisa.sta.repository.UserRepository;
import com.reinertisa.sta.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.reinertisa.sta.utils.UserUtils.createUserEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
//    private final BCryptPasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;


    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, password);
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

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        RoleEntity role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }
}
