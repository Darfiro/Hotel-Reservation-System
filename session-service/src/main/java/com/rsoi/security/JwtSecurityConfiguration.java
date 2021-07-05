package com.rsoi.security;

import com.rsoi.service.UserService;
import exceptions.UserWasAddedException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    @Value("${jwt.type}")
    private String jwtType;
    @Value("${jwt.audience}")
    private String jwtAudience;
    @Autowired
    private UserService userService;

    @Bean
    public UserDetailsService userDetailsService()
    {
        userService.loadDB();
        userService.addUser("admin", "password", "ADMIN");
        return userService.getManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        KeyPair secretKey = Keys.keyPairFor(SignatureAlgorithm.RS256);
        PublicKey publicKey = secretKey.getPublic();
        PrivateKey privateKey = secretKey.getPrivate();
        http.cors().and().csrf().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtAudience, jwtIssuer, privateKey, jwtType, userService))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), publicKey, userService))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().addHeaderWriter((httpServletRequest, httpServletResponse) -> {
            byte[] publicKeyBytes = publicKey.getEncoded();
            httpServletResponse.addHeader(HttpHeaders.CONTENT_ENCODING, new String(java.util.Base64.getEncoder().encode(publicKeyBytes)));
            httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION + "," + HttpHeaders.CONTENT_ENCODING);
        });
    }
}


