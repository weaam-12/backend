package com.ummbatin.service_management.config;

import com.ummbatin.service_management.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity//Allows you to use annotations like @PreAuthorize and @Secured on methods.
public class SecurityConfig {

    @Autowired
    //Injecting the JWT filter to check tokens for each request.
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    //Injecting the custom user service for loading user details.
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    //Injecting the entry point to handle unauthorized access ( return 401 JSON).
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    //Creates a bean for password encoding using BCrypt (hashes passwords securely).
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    //Exposes AuthenticationManager bean, needed for AuthenticationService login.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))/// Enable CORS
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))// Handle unauthorized
                .authorizeHttpRequests(auth -> auth
                        // ✅ مسارات عامة ومفتوحة للجميع
                        .requestMatchers(
                                "/",
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/complaints/**",
                                "/api/users/profile",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        .requestMatchers(
                                "/api/**", // سماح عام لجميع مسارات /api
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/residents/**")//equire ADMIN role only.
                        .hasAnyRole("ADMIN", "RESIDENT")

                        .requestMatchers(HttpMethod.POST, "/api/residents/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/users/profile")//requires any authenticated user.
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/auth/**").permitAll() // ضروري لـ preflight
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())//Sets the custom authentication provider.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)//Adds JWT filter before Spring’s username/password filter.
                .headers(headers -> headers
                        .xssProtection(xss -> xss.disable())//turns off this extra header.
                        .frameOptions(frame -> frame.sameOrigin())//(attackes)Only pages from the same website can show this site in an iframe.”
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://ummbatin-website.onrender.com",
                "http://localhost:3000" // للتطوير المحلي
        ));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));//Exposes Authorization header (needed for JWT).
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);//Cache preflight response for 3600 seconds.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
