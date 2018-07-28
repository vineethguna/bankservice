package com.vineeth.bankservice.controllers;

import com.vineeth.bankservice.security.SecurityManager;
import com.vineeth.bankservice.security.AuthenticationRequest;
import com.vineeth.bankservice.security.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private SecurityManager securityManager;

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse authenticateAdmin(@RequestBody AuthenticationRequest authRequest) throws Exception {
        return securityManager.authenticateAdmin(authRequest);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public AuthenticationResponse authenticateUser(@RequestBody AuthenticationRequest authRequest) throws Exception {
        return securityManager.authenticateUser(authRequest);
    }
}
