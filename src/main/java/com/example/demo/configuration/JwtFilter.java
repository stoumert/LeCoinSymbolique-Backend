package com.example.demo.configuration;

import com.example.demo.Entites.Jwt;
import com.example.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {
    UserService userService;
    JwtService jwtService;
    HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(UserService userService, JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain)
            throws ServletException, IOException
    {
        try {


            String authorization = request.getHeader("Authorization");
            String token;
            String username = null;
            boolean isTokenExpired = true;
            Jwt jwt = null;
            if (authorization != null && authorization.startsWith("Bearer")) {
                token = authorization.substring(7);
                jwt = jwtService.findJwtByToken(token);
                isTokenExpired = jwtService.isTokenExpired(token);
                username = jwtService.extractUsernamme(token);

            }
            if (!isTokenExpired
                    && jwt.getClient().getUsername().equals(username)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch (Exception e) {
            this.handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
