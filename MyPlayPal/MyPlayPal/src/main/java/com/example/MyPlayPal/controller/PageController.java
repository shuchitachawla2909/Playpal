package com.example.MyPlayPal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index"; // maps to templates/index.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // maps to templates/about.html
    }

//    @GetMapping("/contact")
//    public String contact() {
//        return "contact"; // maps to templates/contact.html
//    }

}