package com.vineeth.bankservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vineeth.bankservice.datastore.IBankStore;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SecurityManager {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private IBankStore bankStore;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public SecurityManager(IBankStore bankStore, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bankStore = bankStore;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public AuthenticationResponse authenticateAdmin(AuthenticationRequest request) throws Exception {
        if(checkAdminCredentials(request)) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setUsername(request.getUsername());
            response.setAccessToken(generateJWTToken(generatePayloadForJWT(request,
                    SecurityConstants.ADMIN_SCOPES)));
            return response;
        }
        throw new Exception("Authentication failed for admin");
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest request) throws Exception {
        if(checkUserCredentials(request)) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setUsername(request.getUsername());
            response.setAccessToken(generateJWTToken(generatePayloadForJWT(request,
                    SecurityConstants.USER_SCOPES)));
            return response;
        }
        throw new Exception("Authentication failed for bank user");
    }

    public String authorizeForAdmin(String accessToken) throws Exception {
        AuthorizePayload payload = objectMapper.readValue(verifyJWTToken(accessToken), AuthorizePayload.class);
        if(!payload.getScopes().equals(SecurityConstants.ADMIN_SCOPES)) {
            throw new Exception("Authorization for admin failed");
        }
        return payload.getUsername();
    }

    public String authorizeForUser(String accessToken) throws Exception {
        AuthorizePayload payload = objectMapper.readValue(verifyJWTToken(accessToken), AuthorizePayload.class);
        if(!payload.getScopes().equals(SecurityConstants.USER_SCOPES)) {
            throw new Exception("Authorization for user failed");
        }
        return payload.getUsername();
    }

    private boolean checkAdminCredentials(AuthenticationRequest request) {
        return request.getUsername().equals(SecurityConstants.ADMIN_USER) &&
                request.getPassword().equals(SecurityConstants.ADMIN_PASSWORD);
    }

    private boolean checkUserCredentials(AuthenticationRequest request) {
        AuthenticationDetails authenticationDetailsOfUser =
                bankStore.getAuthenticationDetailsOfUser(request.getUsername());
        return authenticationDetailsOfUser != null &&
                request.getUsername().equals(authenticationDetailsOfUser.getUsername()) &&
                bCryptPasswordEncoder.matches(request.getPassword(), authenticationDetailsOfUser.getPassword());
    }

    private String generatePayloadForJWT(AuthenticationRequest request, String scopes) throws JsonProcessingException {
        JsonNode payload = objectMapper.createObjectNode();
        ((ObjectNode) payload).put(SecurityConstants.JWT.PAYLOAD.KEY_FOR_USERNAME, request.getUsername());
        ((ObjectNode) payload).put(SecurityConstants.JWT.PAYLOAD.KEY_FOR_SCOPES, scopes);
        return objectMapper.writeValueAsString(payload);
    }

    private String generateJWTToken(String payload) {
        return Jwts.builder()
                .setSubject(payload)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.JWT.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT.SECRET)
                .compact();
    }

    private String verifyJWTToken(String accessToken) throws Exception {
        try {

            return Jwts.parser().setSigningKey(SecurityConstants.JWT.SECRET)
                    .parseClaimsJws(accessToken).getBody().getSubject();

        } catch (SignatureException e) {
            throw new Exception("Access token verification failed");
        }
    }
}
