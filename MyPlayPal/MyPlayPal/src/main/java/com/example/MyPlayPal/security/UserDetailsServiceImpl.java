package com.example.MyPlayPal.security;

import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.ManagerRepository;
import com.example.MyPlayPal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepo;
    private final ManagerRepository managerRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting login for username: {}", username);

        // Check User table
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found: {} (password hash: {})", username, user.getPassword());
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        } else {
            logger.info("No user found with username: {}", username);
        }

        // Check Manager table
        Optional<Manager> managerOpt = managerRepo.findByManagername(username);
        if (managerOpt.isPresent()) {
            Manager manager = managerOpt.get();
            logger.info("Manager found: {} (password hash: {})", username, manager.getPassword());
            return org.springframework.security.core.userdetails.User
                    .withUsername(manager.getManagername())
                    .password(manager.getPassword())
                    .roles("MANAGER")
                    .build();
        } else {
            logger.info("No manager found with username: {}", username);
        }

        logger.warn("Login failed: User or Manager not found for username: {}", username);
        throw new UsernameNotFoundException("User or Manager not found: " + username);
    }
}

