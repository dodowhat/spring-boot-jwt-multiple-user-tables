package com.example.scaffold.security;

public class Constants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 10;
    public static final Integer SECRET_LENGTH = 32;
    public static final String ADMIN_AUDIENCE = "admin";
    public static final String APP_AUDIENCE = "app";
}
