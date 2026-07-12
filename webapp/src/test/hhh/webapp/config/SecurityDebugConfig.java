// config/SecurityDebugConfig.java
package com.pfe.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Profile("debug")  // Use profile 'debug' for debugging
public class SecurityDebugConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Add debug logging filter
        http.addFilterBefore(new DebugLoggingFilter(), UsernamePasswordAuthenticationFilter.class);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**").permitAll()  // Allow all API requests for debugging
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Debug filter to log all requests
    public static class DebugLoggingFilter implements Filter {
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DebugLoggingFilter.class);

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, jakarta.servlet.ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            log.info("=== REQUEST DEBUG ===");
            log.info("Method: {}", httpRequest.getMethod());
            log.info("URI: {}", httpRequest.getRequestURI());
            log.info("Query String: {}", httpRequest.getQueryString());
            log.info("Headers:");

            java.util.Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = httpRequest.getHeader(headerName);
                // Mask authorization header partially for security
                if ("authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 20) {
                    headerValue = headerValue.substring(0, 15) + "...";
                }
                log.info("  {}: {}", headerName, headerValue);
            }

            log.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

            // Continue the filter chain
            chain.doFilter(request, response);

            log.info("Response Status: {}", httpResponse.getStatus());
            log.info("===================");
        }
    }
}