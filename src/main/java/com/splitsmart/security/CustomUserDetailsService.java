package com.splitsmart.security;

import com.splitsmart.entity.User;
import com.splitsmart.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        // 1. Find the user using their email.
        // 2. If not found, throw UsernameNotFoundException.
        // 3. Convert the entity into Spring Security's UserDetails.
        // 4. Return it.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("email not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
        return userDetails;
    }
}
//Imagine Alex sends a valid JWT.
//The filter validates it, loads Alex using CustomUserDetailsService,
// and sets the authentication in SecurityContextHolder