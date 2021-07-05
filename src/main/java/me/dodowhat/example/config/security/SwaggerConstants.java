package me.dodowhat.example.config.security;

public class SwaggerConstants {
    public static final String SWAGGER_USERNAME = "swagger";
    public static final String PASSWORD_ENV = "SWAGGER_PASSWORD";
    public static final String DEFAULT_PASSWORD = "password";

    public static String GetSwaggerPassword() {
        String password = System.getenv(PASSWORD_ENV);
        if (password == null) {
            password = DEFAULT_PASSWORD;
        }
        return password;
    }
}
