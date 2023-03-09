package com.example.authservice.model.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserData {
    private String id;
    private String email;
    private String name;
    private String profilePicture;
    private boolean connectStatus;
}
