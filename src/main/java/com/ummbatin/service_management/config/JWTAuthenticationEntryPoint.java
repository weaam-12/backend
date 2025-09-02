
package com.ummbatin.service_management.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
// Marks this class as a Spring Bean, so Spring Security can use it

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // This method is called automatically by Spring Security
    // whenever an unauthorized user tries to access a protected resource
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        // Set the HTTP status code to 401 (Unauthorized)

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Write a JSON response body with error info
        // This includes:
        // - error: a fixed string "Unauthorized"
        // - message: the exception message from Spring Security
        // - path: the URL path the user tried to access
        response.getWriter().write(String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"path\": \"%s\"}",
                authException.getMessage(),//tells why the request failed
                request.getServletPath()//tells which API the user tried to access.
        ));
    }
}
