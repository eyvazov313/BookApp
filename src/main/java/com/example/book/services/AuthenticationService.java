package com.example.book.services;

import com.example.book.config.security.JwtService;
import com.example.book.dto.*;
import com.example.book.entites.Author;
import com.example.book.entites.Reader;
import com.example.book.enums.Role;
import com.example.book.exceptions.InvalidAuthenticationCredentials;
import com.example.book.exceptions.UserAlreadyExists;
import com.example.book.exceptions.UsernameAlreadyExists;
import com.example.book.repositories.AdminRepository;
import com.example.book.repositories.AuthorRepository;
import com.example.book.repositories.ReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthorRepository authorRepository;

    private final ReaderRepository readerRepository;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerForAuthor(AuthorRegisterRequest request) throws UserAlreadyExists {
        if (authorExistsByUsername(request.getUsername())) {
            throw new UserAlreadyExists("Username already exists");
        }
        var author = Author.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.AUTHOR)
                .build();
        authorRepository.save(author);
        var jwtToken = jwtService.generateToken(author);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse registerForReader(ReaderRegisterRequest request) {
        if (readerExistsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExists("Username already exists");
        }
        var reader = Reader.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.READER)
                .build();
        readerRepository.save(reader);
        var jwtToken = jwtService.generateToken(reader);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticateAuthor(AuthorAuthenticationRequest request) {
        if (!validAuthorUsernameAndPassword(request.getUsername(), request.getPassword())) {
            throw new InvalidAuthenticationCredentials("Username or password is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var author = authorRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(author);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticateReader(ReaderAuthenticationRequest request) {
        if (!validReaderUsernameAndPassword(request.getUsername(), request.getPassword())) {
            throw new InvalidAuthenticationCredentials("Username or password is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var reader = readerRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(reader);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticateAdmin(AdminAuthenticationRequest request) {
        if (!validAdminUsernameAndPassword(request.getUsername(), request.getPassword())) {
            throw new InvalidAuthenticationCredentials("Username or password is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(admin);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public boolean authorExistsByUsername(String username) {
        Optional<Author> author = authorRepository.findByUsername(username);
        return author.isPresent();
    }

    public boolean readerExistsByUsername(String username) {
        Optional<Reader> reader = readerRepository.findByUsername(username);
        return reader.isPresent();
    }

    public boolean validAuthorUsernameAndPassword(String username, String password) {
        var author = authorRepository.findByUsername(username);
        if (author.isPresent()) {
            String storedHashedPassword = author.get().getPassword();
            return BCrypt.checkpw(password, storedHashedPassword);
        }
        return false;
    }


    public boolean validReaderUsernameAndPassword(String username, String password) {
        var reader = readerRepository.findByUsername(username);
        if (reader.isPresent()) {
            String storedHashedPassword = reader.get().getPassword();
            return BCrypt.checkpw(password, storedHashedPassword);
        }
        return false;
    }

    public boolean validAdminUsernameAndPassword(String username, String password) {
        var admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            String storedHashedPassword = admin.get().getPassword();
            return BCrypt.checkpw(password, storedHashedPassword);
        }
        return false;
    }
}
