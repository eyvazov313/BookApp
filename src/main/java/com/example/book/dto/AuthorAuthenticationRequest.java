package com.example.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorAuthenticationRequest {

    @NotNull(message = "username can not be null")
    @NotBlank(message = "username can not be empty")
    private String username;

    @NotNull(message = "username can not be null")
    @NotBlank(message = "username can not be empty")
    private String password;
}
