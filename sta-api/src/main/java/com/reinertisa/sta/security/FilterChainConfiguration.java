package com.reinertisa.sta.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class FilterChainConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/user/login")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        MyOwnAuthenticationProvider myOwnAuthenticationProvider = new MyOwnAuthenticationProvider(userDetailsService);
        return new ProviderManager(myOwnAuthenticationProvider);
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        var user1 = User.withDefaultPasswordEncoder()
//                .username("bilirkishi")
//                .password("{noop}letmein")
//                .roles("USER")
//                .build();
//
//        var user2 = User.withDefaultPasswordEncoder()
//                .username("bulgurcu")
//                .password("{noop}letmein")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(List.of(user1, user2));
//    }

    @Bean
    public UserDetailsService inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("bilirkishi").password("letmein").roles("USER").build(),
                User.withUsername("bulgurcu").password("letmein").roles("USER").build()
        );
    }
}
