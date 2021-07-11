package me.dodowhat.example.dto.admin.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdatePasswordRequestDTO {
    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@$%^&*()\\-=_+|\\[\\]{};:,./<>?]).{8,32}$",
            message = "at least one digit, lowercase character, uppercase character, special character")
    private String newPassword;
}
