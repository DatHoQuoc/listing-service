package com.dathq.swd302.listingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.dathq.swd302.listingservice.common.constant.AppConstants.API_BASE;

@Configuration
public class BeansConfig {

//
//    @Bean
//    public CorsFilter corsFilter(){
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(Collections.singletonList(API_BASE));
//        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedMethods(Arrays.asList(
//                "GET",
//                "POST",
//                "PUT",
//                "DELETE",
//                "PATCH",
//                "OPTIONS"
//        ));
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
}
