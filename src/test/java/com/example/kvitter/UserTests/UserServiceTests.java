package com.example.kvitter.UserTests;

import com.example.kvitter.Kvitter.Kvitter;
import com.example.kvitter.Kvitter.KvitterService;
import com.example.kvitter.User.User;
import com.example.kvitter.User.UserRepo;
import com.example.kvitter.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepo userRepo;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;
    
    @InjectMocks
    private KvitterService kvitterService;

    private UUID userId = UUID.randomUUID();
    private String userEmail = "mario.bros@nintendo.com";
    private String userPassword = "itsame123";
    private String userFirstName = "Mario";
    private String userLastName = "Bros";
    private List<Kvitter> kvitterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_SuccessfulSave() {
        String email = "test@example.com";
        String password = "password123";
        String firstName = "John";
        String lastName = "Doe";
        UserService userService1 = new UserService(userRepo);
        when(userRepo.findByEmail(email)).thenReturn(null);

        userService1.addUser(email, password, firstName, lastName);
        verify(userRepo, times(1)).findByEmail(email);
        verify(userRepo, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getFirstName().equals(firstName) &&
                        user.getLastName().equals(lastName)
        ));
    }

    @Test
    void addUser_EmailAlreadyUsed() {
        String email = "test@example.com";
        User existingUser = User.builder()
                .email(email)
                .password("hashed_password")
                .firstName("Existing")
                .lastName("User")
                .build();

        lenient().when(userRepo.findByEmail(email)).thenReturn(existingUser);

        userService.addUser(email, "password123", "John", "Doe");

        verify(userRepo, never()).save(any());
    }
}
