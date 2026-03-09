//package com.dathq.swd302.listingservice.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpHeaders;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static com.dathq.swd302.listingservice.common.constant.AppConstants.API_BASE;
//
//@Configuration
//public class BeansConfig {
//
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowedMethods(List.of("*"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//
//        System.out.println(">>> CORS FILTER REGISTERED <<<"); // 👈 add this
//        return bean;
//    }
//}
