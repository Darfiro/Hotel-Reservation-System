package com.rsoi.controller;

import com.rsoi.model.User;
import com.rsoi.service.DatabaseService;
import exceptions.UserWasAddedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sessionreqres.GetUser;
import sessionreqres.NewUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "")
public class SessionController
{
    @Autowired
    private DatabaseService db;

    @GetMapping("/verify")
    public void verify()
    {
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Iterable<GetUser>> getAllPersons()
    {
        Iterable<User> usersList =  db.getUsers();
        List<GetUser> getUsers = new ArrayList<>();
        for (User user : usersList)
        {
            GetUser getUser = new GetUser();
            getUser.setUserUid(user.getUserUid().toString());
            getUser.setLogin(user.getLogin());
            getUser.setRole(user.getGrantedAuthorities().getAuthority());
            getUsers.add(getUser);
        }
        return ResponseEntity.ok(getUsers);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity postUser(@RequestBody NewUser addedUser) throws UserWasAddedException {
        if (addedUser.getLogin() == null || addedUser.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (addedUser.getRole() == null)
            addedUser.setRole("USER");
        User user = null;
        user = db.saveUser(addedUser);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
