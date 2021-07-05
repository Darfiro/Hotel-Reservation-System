package com.rsoi.hotels.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ServiceUserService {
    public Collection<? extends GrantedAuthority> getUserAuthority(String username) {
        if (username.equals("admin")) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            return authorities;
        }
        return new ArrayList<>();
    }
}
