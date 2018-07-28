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

    public User addUser(UserRequest userRequest) throws Exception {
        userRequest.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        String accountId = bankStore.addUser(userRequest.getUsername(), userRequest.getPassword(),
                userRequest.getBalance());
        if(accountId == null) {
            throw new Exception("User already exists");
        }
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setAccountId(accountId);
        return user;
    }

    public User getUserDetails(String username) throws Exception {
        User user = bankStore.getUserDetails(username);
        if(user == null) {
            throw new Exception("Given user does not exist");
        }
        return user;
    }
}
