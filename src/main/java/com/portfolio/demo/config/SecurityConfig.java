package com.portfolio.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.portfolio.demo.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
    

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(CustomUserDetailsService customUserDetailsService, 
                        JwtAuthenticationFilter jwtAuthenticationFilter){
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = 
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable())  // Disable CSRF
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/login", "/signup", "/").permitAll()  // Allow unauthenticated access
                    .requestMatchers("/employer-dashboard/**").hasAuthority("EMPLOYER")  // EMPLOYER-specific routes
                    .requestMatchers("/employee-dashboard/**").hasAuthority("EMPLOYEE") // EMPLOYEE-specific routes
                    .anyRequest().authenticated()  // All other routes require authentication
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless sessions
            ).logout(logout -> logout
            .logoutUrl("/logout")  // Custom logout URL
            .invalidateHttpSession(true)  // Invalidate session
            .deleteCookies("token")  // Explicitly delete the token cookie
            .logoutSuccessUrl("/login?logout=true")  // Redirect after logout
            )
            .authenticationManager(authenticationManager(http))  // Custom AuthenticationManager
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }
}
