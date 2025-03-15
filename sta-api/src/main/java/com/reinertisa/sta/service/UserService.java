package com.reinertisa.sta.service;

import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.entity.CredentialEntity;
import com.reinertisa.sta.entity.RoleEntity;
import com.reinertisa.sta.enumaration.LoginType;

public interface UserService {
    void createUser(String firstName, String lastName, String email, String password);
    RoleEntity getRoleName(String name);
    void verifyAccountKey(String key);
    void updateLoginAttempt(String email, LoginType loginType);
    User getUserByUserId(String userId);
    User getUserByEmail(String email);
    CredentialEntity getUserCredentialById(Long id);
    User setUpMfa(Long id);
    User cancelMfa(Long id);
    User verifyQrCode(String userId, String qrCode);
    void resetPassword(String email);
    User verifyPasswordKey(String key);
    User updatePassword(String userId, String newPassword, String confirmNewPassword);
}
