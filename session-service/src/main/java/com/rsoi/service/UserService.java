package com.rsoi.service;

import com.rsoi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import sessionreqres.NewUser;
import exceptions.UserWasAddedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService
{
    @Autowired
    DatabaseService db;

    public User findUserByUsername(final String login)
    {
        User user = db.getUserByLogin(login);
        return user;
    }

    public Collection<? extends GrantedAuthority> getUserAuthority(String username)
    {
        User user = db.getUserByLogin(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(user.getGrantedAuthorities());
        return authorities;
    }

    public void loadDB() {
        db.loadDB();
    }

    public void addUser(String login, String password, String role) {
        NewUser user = new NewUser();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(role);
        try {
            User temp = db.saveUser(user);
        } catch (UserWasAddedException e) {
            System.out.println(e.getMessage());
        }
    }

    public InMemoryUserDetailsManager getManager()
    {
        return db.getManager();
    }

}

