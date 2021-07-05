package me.dodowhat.example.config.security;

public class JwtConstants {
    public static final String DEFAULT_SECRET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ012345";
    public static final String SECRET_ENV = "JWT_SECRET";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int SECRET_LENGTH = 32;
    public static final long ACCESS_TOKEN_DURATION = 1000L * 60 * 60 * 24 * 10; // days in milliseconds
    public static final long REFRESH_TOKEN_DURATION = 1000L * 60 * 60 * 24 * 30; // days in milliseconds
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String ADMIN_AUDIENCE = "Admin";
    public static final String APP_AUDIENCE = "App";
    public static final String ACCESS_TOKEN_TYPE = "Access Token";
    public static final String REFRESH_TOKEN_TYPE = "Refresh Token";

    public static String GetJWTSecret() {
        String secretString = System.getenv(SECRET_ENV);
        if (secretString == null) {
            secretString = DEFAULT_SECRET;
        }
        return secretString;
    }
}
