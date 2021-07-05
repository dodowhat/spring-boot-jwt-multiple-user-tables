package me.dodowhat.example.dto.admin.auth;

import lombok.Data;

import java.util.List;

@Data
public class ShowResponseDTO {
    private long id;
    private String username;
    private List<String> roles;
}
