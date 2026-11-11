package com.splitsmart.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

//    // Read Authorization header
//    // Check whether it contains a Bearer token
//    // Extract the token
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        // 1. Read the Authorization header
//        String header = request.getHeader("Authorization");
//
//        // 2. Check whether a Bearer token exists
//        if (header != null && header.startsWith("Bearer ")) {
//
//            // 3. Extract the actual JWT
//            String jwt = header.substring(7);
//
//            // We'll validate this token in the next step.
//            String email = jwtService.extractEmail(jwt);
//
//            if (email != null &&
//                    SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                UserDetails userDetails =
//                        userDetailsService.loadUserByUsername(email);
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//                //We've validated the JWT, found Alex in MySQL, and created an authentication object.
//                //But how does the rest of Spring Security know about Alex?
//                //We put that authentication object into the SecurityContext.
//                //SecurityContextHolder provides access to the current security context.
//                //Makes the current context accessible to Spring Security
//                SecurityContextHolder.getContext()
//                        .setAuthentication(authentication);
//            }
//        }
//
//        // 4. Continue the request through the filter chain
//        filterChain.doFilter(request, response);
//    }

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7);

        try {
            //Verify the token and given email
            String email = jwtService.extractEmail(jwt);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);
                // 1. Build the fresh security badge using data loaded from MySQL
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException |
                 UsernameNotFoundException e) {

            log.error("JWT authentication failed: {}", e.getMessage(), e);

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

//catch (JwtException | IllegalArgumentException |
//                 UsernameNotFoundException e) {
//
//            SecurityContextHolder.clearContext();
//        }