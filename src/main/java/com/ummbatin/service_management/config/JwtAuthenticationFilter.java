package com.ummbatin.service_management.config;

import com.ummbatin.service_management.services.CustomUserDetailsService;
import com.ummbatin.service_management.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
// Runs **once per request** to check JWT token before Spring Security allows access

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;//Utility to extract/validate JWt
    private final CustomUserDetailsService userDetailsService;/// Loads user from DB by email

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    // This method runs for **every HTTP request**

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            //Extract JWT from the "Authorization" header
            String jwt = parseJwt(request);
            //If JWT exists and no authentication is set in Spring context yet

            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Get username/email from the JWT
                String username = jwtUtil.extractUsername(jwt);
                //  Load user details from DB (email + password + roles)

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Validate JWT (signature, expiration, username match)

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    //  Extract the role from the token
                    String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

                    //  Prefix with "ROLE_" to match Spring Security format
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    //  Create an authentication object for Spring Security

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //Save authentication to Spring Security context
                    //    So future controllers know the user is authenticated
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException ex) {
            // JWT expired

            logger.error("JWT token expired: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
            return;
        } catch (JwtException ex) {
            // Any other JWT error (malformed, signature invalid)

            logger.error("JWT token invalid: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (Exception ex) {
            logger.error("Authentication error: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }
    // Helper method to extract JWT from "Authorization" header

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        // Only return JWT if header starts with "Bearer "

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }
}
