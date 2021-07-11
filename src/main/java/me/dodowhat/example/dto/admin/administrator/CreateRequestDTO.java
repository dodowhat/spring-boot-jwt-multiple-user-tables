package me.dodowhat.example.dto.admin.administrator;

import lombok.Data;
import me.dodowhat.example.validator.RbacNameUnique;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateRequestDTO {
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[A-Za-z0-9]{3,}$", message = "must be alpha or numbers or both")
    @RbacNameUnique
    private String username;

    @NotBlank
    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@$%^&*()\\-=_+|\\[\\]{};:,./<>?]).{8,32}$",
            message = "at least one digit, lowercase character, uppercase character, special character")
    private String password;
}
