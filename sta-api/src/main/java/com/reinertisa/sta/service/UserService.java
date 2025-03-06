package com.reinertisa.sta.service;

import com.reinertisa.sta.entity.RoleEntity;

public interface UserService {
    void createUser(String firstName, String lastName, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);
}
