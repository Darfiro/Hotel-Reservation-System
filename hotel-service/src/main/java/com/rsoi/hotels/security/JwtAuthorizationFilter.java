package com.rsoi.hotels.security;

import com.rsoi.hotels.service.ServiceUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private PublicKey jwtSecret;
    private ServiceUserService userService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  PublicKey jwtSecret, ServiceUserService userService) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = parseToken(request);

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            String claims = token.replace("Bearer ", "");
            try {
                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(jwtSecret)
                        .parseClaimsJws(claims);

                String username = claimsJws.getBody().getSubject();

                if ("".equals(username) || username == null) {
                    return null;
                }
                return new UsernamePasswordAuthenticationToken(username, null, userService.getUserAuthority(username));
            } catch (JwtException exception) {
                throw exception;
            }
        }
        return null;
    }
}
