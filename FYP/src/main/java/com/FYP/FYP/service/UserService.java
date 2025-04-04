package com.FYP.FYP.service;

import com.FYP.FYP.model.User;
import com.FYP.FYP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session; 

    public boolean validateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) { 
                session.setAttribute("loggedInUser", user);
                return true;
            }
        }
        return false;
    }

    public User getLoggedInUser() {
        return (User) session.getAttribute("loggedInUser");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void logoutUser() {
        session.invalidate();
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }
}
