package com.rsoi.payments.security;

import com.rsoi.payments.service.ServiceUserService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.security.KeyPair;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    @Value("${jwt.type}")
    private String jwtType;
    @Value("${jwt.audience}")
    private String jwtAudience;
    @Autowired
    private ServiceUserService userService;

    @Bean
    public UserDetailsService userDetailsService()
    {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users.username("admin").password("password").roles("ADMIN").build());
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        KeyPair secretKey = Keys.keyPairFor(SignatureAlgorithm.RS256);
        http.cors().and().csrf().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtAudience, jwtIssuer, secretKey.getPrivate(), jwtType))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), secretKey.getPublic(), userService))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}


