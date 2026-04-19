package com.example.demo.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username ko co dau cach, tieng viet hoac ki tu dac biet la")
    private String username;
    @NotBlank(message = "Password is not blank")
    private String password;
}
