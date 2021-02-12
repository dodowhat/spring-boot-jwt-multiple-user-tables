package com.example.scaffold.response;

public class AdminUserResetPasswordResponseBody {
    private String password;

    public AdminUserResetPasswordResponseBody(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
