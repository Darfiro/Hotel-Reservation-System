package com.rsoi.service;

import com.rsoi.model.User;
import com.rsoi.repository.UserRepository;
import exceptions.UserWasAddedException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import sessionreqres.NewUser;

import java.util.Optional;

@Service
public class DatabaseService
{
    @Autowired
    private UserRepository userRepository;
    @Getter
    private InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    private org.springframework.security.core.userdetails.User.UserBuilder users =
            org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder();


    public Iterable<User> getUsers()
    {
        return userRepository.findAll();
    }

    public void loadDB() {
        Iterable<User> usersDB = getUsers();
        for (User user : usersDB) {
            manager.createUser(users.username(user.getLogin()).password(user.getPassword()).roles(user.getGrantedAuthorities().getAuthority()).build());
        }
    }

    public User saveUser(NewUser addedUser) throws UserWasAddedException {
        if (getUserByLogin(addedUser.getLogin()) == null) {
            User user = new User();
            user.setLogin(addedUser.getLogin());
            user.setPassword(addedUser.getPassword());
            user.setGrantedAuthorities(new SimpleGrantedAuthority(addedUser.getRole()));
            manager.createUser(users.username(addedUser.getLogin()).password(addedUser.getPassword()).roles(addedUser.getRole()).build());
            return userRepository.save(user);
        } else {
            throw new UserWasAddedException("User with that login already exist. Login: " + addedUser.getLogin());
        }
    }

    public User getUserByLogin(String login)
    {
        Optional<User> userOptional = userRepository.findByLogin(login);
        User found = null;
        if (userOptional.isPresent())
        {
            found = userOptional.get();
        }
        return found;
    }

}
