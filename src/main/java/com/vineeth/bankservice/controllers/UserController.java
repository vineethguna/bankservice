package com.vineeth.bankservice.controllers;

import com.vineeth.bankservice.security.SecurityManager;
import com.vineeth.bankservice.user.User;
import com.vineeth.bankservice.user.UserManager;
import com.vineeth.bankservice.user.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private UserManager userManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserRequest user, @RequestHeader("Authorization") String accessToken)
            throws Exception {
        securityManager.authorizeForAdmin(accessToken);
        return userManager.addUser(user);
    }

    @RequestMapping("/{username}")
    public User getUserDetails(@PathVariable("username") String username,
                               @RequestHeader("Authorization") String accessToken) throws Exception {
        securityManager.authorizeForUser(accessToken);
        return userManager.getUserDetails(username);
    }
}
