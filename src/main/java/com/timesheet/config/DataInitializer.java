package com.timesheet.config;

import com.timesheet.entity.User;
import com.timesheet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default manager account if no users exist
        if (userRepository.count() == 0) {
            User manager = new User();
            manager.setUsername("admin");
            manager.setPassword(passwordEncoder.encode("admin123"));
            manager.setFullName("System Administrator");
            manager.setEmail("admin@company.com");
            manager.setRole(User.Role.MANAGER);
            
            userRepository.save(manager);
            
            System.out.println("Default manager account created:");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("Email: admin@company.com");
        }
    }
} 