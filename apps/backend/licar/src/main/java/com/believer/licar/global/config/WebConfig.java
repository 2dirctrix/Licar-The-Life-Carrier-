package com.believer.licar.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://j13c206.p.ssafy.io")
                .allowedMethods("GET", "POST", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
