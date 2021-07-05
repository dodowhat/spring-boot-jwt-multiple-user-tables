package me.dodowhat.example.dto.admin.administrator;

import lombok.Data;

@Data
public class ResetPasswordResponseDTO {
    private String password;

    public ResetPasswordResponseDTO(String password) {
        this.password = password;
    }
}
