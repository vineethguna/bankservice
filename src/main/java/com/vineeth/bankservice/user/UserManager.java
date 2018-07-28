package com.vineeth.bankservice.user;

import com.vineeth.bankservice.datastore.IBankStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserManager {
    private IBankStore bankStore;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserManager(IBankStore bankStore, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bankStore = bankStore;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void addUser(UserRequest userRequest) throws Exception {
        userRequest.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        if(!bankStore.addUser(userRequest)) {
            throw new Exception("User already exists");
        }
    }
}
