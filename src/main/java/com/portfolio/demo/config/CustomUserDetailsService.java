package com.portfolio.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.portfolio.demo.model.User;
import com.portfolio.demo.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("Username not found: @"+username);
        }

        String role = user.getUserIs().name();

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(), 
            AuthorityUtils.createAuthorityList(role)
        );
    }
}
