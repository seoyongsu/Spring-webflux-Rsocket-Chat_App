package com.example.authservice.model.auth.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SignUpRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;


}
