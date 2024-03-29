package com.example.book.dto;

import lombok.Data;

@Data
public class AuthorDetailsResponse {

    private Long id;

    private String name;

    private String surname;

    private String username;
}
