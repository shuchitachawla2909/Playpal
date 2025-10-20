package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ManagerSignupRequest;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.repository.ManagerRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.ManagerService;
import com.example.MyPlayPal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthFormController {

    private final UserService userService;
    private final ManagerService managerService;
    private final UserRepository userRepo;
    private final ManagerRepository managerRepo;

    @Autowired
    public AuthFormController(UserService userService,
                              ManagerService managerService,
                              UserRepository userRepo,
                              ManagerRepository managerRepo) {
        this.userService = userService;
        this.managerService = managerService;
        this.userRepo = userRepo;
        this.managerRepo = managerRepo;
    }

    // ---------- GET: signup page ----------
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("userSignupRequest", new UserSignupRequest());
        model.addAttribute("managerSignupRequest", new ManagerSignupRequest());
        return "signup";
    }

    // ---------- POST: signup ----------
    @PostMapping("/signup")
    public String signupSubmit(@RequestParam("role") String role,
                               @ModelAttribute("userSignupRequest") UserSignupRequest userReq,
                               @ModelAttribute("managerSignupRequest") ManagerSignupRequest managerReq,
                               Model model) {

        try {
            if ("USER".equalsIgnoreCase(role)) {
                // Check uniqueness in user table
                if (userRepo.findByUsername(userReq.getUsername()).isPresent()) {
                    model.addAttribute("error", "Username already exists for user");
                    return "signup";
                }
                if (userRepo.findByEmail(userReq.getEmail()).isPresent()) {
                    model.addAttribute("error", "Email already exists for user");
                    return "signup";
                }

                userService.createUser(userReq);
                return "redirect:/login";

            } else if ("MANAGER".equalsIgnoreCase(role)) {
                // Check uniqueness in manager table
                if (managerRepo.findByName(managerReq.getName()).isPresent()) {
                    model.addAttribute("error", "Manager name already exists");
                    return "signup";
                }
                if (managerRepo.findByEmail(managerReq.getEmail()).isPresent()) {
                    model.addAttribute("error", "Email already exists for manager");
                    return "signup";
                }

                managerService.createManager(managerReq);
                return "redirect:/login";

            } else {
                model.addAttribute("error", "Invalid role selection");
                return "signup";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Signup failed: " + e.getMessage());
            return "signup";
        }
    }

    // ---------- GET: login page ----------
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

