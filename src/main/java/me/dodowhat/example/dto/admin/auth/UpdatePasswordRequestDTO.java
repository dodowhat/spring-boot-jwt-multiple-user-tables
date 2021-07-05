package me.dodowhat.example.dto.admin.auth;

import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {
    private String password;
    private String newPassword;
}
