package com.reinertisa.sta.service;

public interface EmailService {
    void setNewAccountEmail(String name, String email, String token);
    void sendPasswordResetEmail(String name, String email, String token);
}
