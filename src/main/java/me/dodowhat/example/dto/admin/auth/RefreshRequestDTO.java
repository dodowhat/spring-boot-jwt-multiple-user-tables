package me.dodowhat.example.dto.admin.auth;

import lombok.Data;

@Data
public class RefreshRequestDTO {
    private String refreshToken;
    private String accessToken;
}
