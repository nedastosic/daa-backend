package com.fon.neda.da.service;

import com.fon.neda.da.entity.User;
import com.fon.neda.da.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@ComponentScan
public class UserService {
    @Autowired
    public UserRepository userRepository;


    public List<User> findAllUsers(){
        return (List<User>) userRepository.findAll();
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
