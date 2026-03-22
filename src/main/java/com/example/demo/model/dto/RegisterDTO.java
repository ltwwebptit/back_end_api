package com.example.demo.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "Email is not blank")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Username is not blank")
    private String username;
    @NotBlank(message = "Password is not blank")
    private String password;
}
