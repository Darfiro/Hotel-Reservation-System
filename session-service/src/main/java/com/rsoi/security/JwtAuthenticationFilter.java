package com.rsoi.security;

import com.rsoi.service.DatabaseService;
import com.rsoi.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private String jwtAudience;
    private String jwtIssuer;
    private PrivateKey jwtSecret;
    private String jwtType;
    private UserService userService;
    private BasicAuthenticationConverter authenticationConverter = new BasicAuthenticationConverter();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   String jwtAudience, String jwtIssuer,
                                   PrivateKey jwtSecret, String jwtType, UserService service)
    {
        this.jwtAudience = jwtAudience;
        this.jwtIssuer = jwtIssuer;
        this.jwtSecret = jwtSecret;
        this.jwtType = jwtType;
        this.userService = service;
        this.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/auth");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Basic "))
        {
            UsernamePasswordAuthenticationToken authRequest = authenticationConverter.convert(request);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        else
            throw new AuthenticationServiceException("No Basic Auth header found");
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain, Authentication authentication)
    {
        User user = (User)authentication.getPrincipal();
        com.rsoi.model.User dbUser = userService.findUserByUsername(user.getUsername());
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userUid", dbUser.getUserUid());
        newClaims.put("role", dbUser.getGrantedAuthorities().getAuthority());
        String token = Jwts.builder()
                .signWith(jwtSecret, SignatureAlgorithm.RS256)
                .setHeaderParam("typ", jwtType)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setSubject(user.getUsername())
                .addClaims(newClaims)
                .setExpiration(new Date(System.currentTimeMillis() + 2000000))
                .compact();

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}