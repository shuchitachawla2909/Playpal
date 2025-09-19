package com.playpal.dbms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //ya to create this jwtinterceptor class baad mein after checking from shikhar bhaiya repo or figure out how to make private auth
//    @Autowired
//    private JwtInterceptor jwtInterceptor;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/doctor/**", "/patients/**") // Apply JWT to doctor and patient endpoints
//                .excludePathPatterns("/auth/**"); // Exclude login and registration endpoints
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST","PATCH", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}