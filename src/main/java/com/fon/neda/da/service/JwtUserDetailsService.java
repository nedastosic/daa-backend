package com.fon.neda.da.service;


import com.fon.neda.da.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*if ("admin".equals(username)) {
            return new User("admin", "$2y$12$A2ONG0dsKeXKfag.02w3xe3j0nVcHRsWkHwWQnu7N1zjWjhYLb3Km",


                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }*/
        com.fon.neda.da.entity.User user = userRepository.findByUsername(username);
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }



}
