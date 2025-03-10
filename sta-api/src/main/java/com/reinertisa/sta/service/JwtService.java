package com.reinertisa.sta.service;

import com.reinertisa.sta.domain.Token;
import com.reinertisa.sta.domain.TokenData;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.enumaration.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);
    void addCookie(HttpServletResponse response, User user, TokenType type);
    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
