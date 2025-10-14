package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateUserRequest;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthFormController {

    private final UserService userService;
    private final UserRepository userRepo;

    @Autowired
    public AuthFormController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("createUserRequest") CreateUserRequest request,
                               Model model) {
        // uniqueness checks (optional)
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists");
            return "signup";
        }

        userService.createUser(request); // service should encode password & save
        return "redirect:/login";
    }

    // ---------- GET: login page ----------
    // Spring Security will handle POST /login automatically if configured with formLogin()
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "login";
    }
}

