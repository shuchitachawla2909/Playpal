package com.example.MyPlayPal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {
    private static final Logger log = LoggerFactory.getLogger(DebugController.class);

    @PostMapping("/raw")
    public ResponseEntity<String> raw(HttpServletRequest request) throws Exception {
        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        log.info("RAW BODY:\n{}", body);
        return ResponseEntity.ok("ok");
    }
}
