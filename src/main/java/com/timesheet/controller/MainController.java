package com.timesheet.controller;

import com.timesheet.dto.UserRegistrationDto;
import com.timesheet.entity.User;
import com.timesheet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDto userRegistrationDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        try {
            userService.registerUser(userRegistrationDto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    // Debug endpoint to check users in database
    @GetMapping("/debug/users")
    @ResponseBody
    public String debugUsers() {
        List<User> users = userService.findAllUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("Users in database:\n");
        for (User user : users) {
            sb.append("ID: ").append(user.getId())
              .append(", Username: ").append(user.getUsername())
              .append(", Role: ").append(user.getRole())
              .append(", Password: ").append(user.getPassword().substring(0, Math.min(20, user.getPassword().length()))).append("...")
              .append("\n");
        }
        return sb.toString();
    }
    
    // Test authentication endpoint
    @GetMapping("/debug/test-auth")
    @ResponseBody
    public String testAuth() {
        StringBuilder sb = new StringBuilder();
        sb.append("Testing authentication:\n");
        
        // Test password encoding
        String testPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(testPassword);
        sb.append("Test password: ").append(testPassword).append("\n");
        sb.append("Encoded password: ").append(encodedPassword).append("\n");
        sb.append("Password matches: ").append(passwordEncoder.matches(testPassword, encodedPassword)).append("\n");
        
        // Check if admin user exists
        List<User> users = userService.findAllUsers();
        User adminUser = users.stream()
                .filter(u -> "admin".equals(u.getUsername()))
                .findFirst()
                .orElse(null);
        
        if (adminUser != null) {
            sb.append("Admin user found:\n");
            sb.append("Username: ").append(adminUser.getUsername()).append("\n");
            sb.append("Password: ").append(adminUser.getPassword()).append("\n");
            sb.append("Password matches admin123: ").append(passwordEncoder.matches("admin123", adminUser.getPassword())).append("\n");
        } else {
            sb.append("Admin user NOT found!\n");
        }
        
        return sb.toString();
    }
    
    // Create admin user manually
    @GetMapping("/debug/create-admin")
    @ResponseBody
    public String createAdmin() {
        try {
            // Check if admin already exists
            List<User> users = userService.findAllUsers();
            User existingAdmin = users.stream()
                    .filter(u -> "admin".equals(u.getUsername()))
                    .findFirst()
                    .orElse(null);
            
            if (existingAdmin != null) {
                return "Admin user already exists with ID: " + existingAdmin.getId();
            }
            
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@company.com");
            admin.setRole(User.Role.MANAGER);
            
            User savedAdmin = userService.createManager(new UserRegistrationDto("admin", "admin123", "System Administrator", "admin@company.com"));
            
            return "Admin user created successfully with ID: " + savedAdmin.getId() + 
                   "\nUsername: admin" +
                   "\nPassword: admin123" +
                   "\nRole: " + savedAdmin.getRole();
        } catch (Exception e) {
            return "Error creating admin user: " + e.getMessage();
        }
    }
} 