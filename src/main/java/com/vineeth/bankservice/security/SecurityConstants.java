package com.vineeth.bankservice.security;

import com.vineeth.bankservice.EnvironmentConstants;

public class SecurityConstants {
    public static class JWT {
        public static final String SECRET = System.getenv(EnvironmentConstants.JWT_SECRET);
        public static final long EXPIRATION_TIME = 864000;

        public static class PAYLOAD {
            public static final String KEY_FOR_USERNAME = "username";
            public static final String KEY_FOR_SCOPES = "scopes";
        }
    }

    public static final String ADMIN_USER_TYPE = "admin_user";
    public static final String NORMAL_USER_TYPE = "normal_user";

    public static final String ADMIN_USER = System.getenv(EnvironmentConstants.ADMIN_USERNAME);
    public static final String ADMIN_PASSWORD = System.getenv(EnvironmentConstants.ADMIN_PASSWORD);
    public static final String ADMIN_SCOPES = "admin";
    public static final String USER_SCOPES = "user";
}
