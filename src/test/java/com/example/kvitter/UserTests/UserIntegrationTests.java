package com.example.kvitter.UserTests;

import com.example.kvitter.Kvitter.Kvitter;
import com.example.kvitter.User.User;
import com.example.kvitter.User.UserRepo;
import com.example.kvitter.User.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserIntegrationTests {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    private UUID userId = UUID.randomUUID();
    private String userEmail = "mario.bros@nintendo.com";
    private String userPassword = "itsame123";
    private String userFirstName = "Mario";
    private String userLastName = "Bros";
    private List<Kvitter> kvitterList = new ArrayList<>();
    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        User user = new User(userEmail, userPassword, userFirstName, userLastName, kvitterList);
        userRepo.save(user);
    }


    @Test
    void testFindUserByEmail() {
        User user = userRepo.findByEmail(userEmail);
        assertNotNull(user);
        assertEquals(userEmail, user.getEmail());
    }


}
