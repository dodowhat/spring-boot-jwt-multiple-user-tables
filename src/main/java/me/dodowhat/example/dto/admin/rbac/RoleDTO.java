package me.dodowhat.example.dto.admin.rbac;

import lombok.Data;
import me.dodowhat.example.validator.RbacNameUnique;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RoleDTO {
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[A-Za-z0-9]{3,}$", message = "must be alpha or numbers or both")
    @RbacNameUnique
    private String name;
}
