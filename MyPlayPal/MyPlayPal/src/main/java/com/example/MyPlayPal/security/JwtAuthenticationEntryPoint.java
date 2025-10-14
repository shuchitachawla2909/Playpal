//package com.example.MyPlayPal.security;
//
//import jakarta.servlet.http.*;
//import org.slf4j.*;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Map;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException {
//        log.info("Authentication failed: {}", authException.getMessage());
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        Map<String, Object> body = Map.of(
//                "timestamp", java.time.Instant.now().toString(),
//                "status", 401,
//                "error", "Unauthorized",
//                "message", authException.getMessage(),
//                "path", request.getRequestURI()
//        );
//        new ObjectMapper().writeValue(response.getOutputStream(), body);
//    }
//}
