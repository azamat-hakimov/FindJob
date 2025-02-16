package com.portfolio.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.portfolio.demo.config.CustomUserDetailsService;
import com.portfolio.demo.model.User;
import com.portfolio.demo.repository.UserRepository;
import com.portfolio.demo.security.JwtTokenUtil;
@Service
public class AuthenticationService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    // Helper method to authenticate and retrieve the user
    public User authenticateAndGetUser(String token) {
        if (token != null && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.extractUsername(token);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );

                return userRepository.findByUsername(username);
        }
        return null; // Return null if token is invalid
    }
}
