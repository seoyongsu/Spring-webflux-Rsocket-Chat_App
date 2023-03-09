package com.example.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResult {
    private Boolean success;
    private String message;
}
