package com.example.authservice.model.user.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * User Entity
 */
@Getter
@Document
public class User {

    @Id
    private String id;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;

    private String mobile;

    private String password;

    private String name;

    private String providerType;

    @CreatedDate
    private LocalDateTime createAt;
    @LastModifiedDate
    private LocalDateTime updateAt;


    @Builder
    public User(String email, String mobile, String password, String name, String providerType){
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.name = name;
        this.providerType = providerType;
    }




}
