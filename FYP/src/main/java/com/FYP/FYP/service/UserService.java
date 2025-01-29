package com.FYP.FYP.service;

import com.FYP.FYP.model.User;
import com.FYP.FYP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public boolean validateUser(String email, String password) {
        return userRepository.findByEmail(email)
            .map(user -> user.getPassword().equals(password)) // Compare passwords
            .orElse(false); // Return false if user is not found
    }
}
