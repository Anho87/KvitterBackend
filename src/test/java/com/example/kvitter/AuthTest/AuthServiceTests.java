package com.example.kvitter.AuthTest;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");
    }

    @Test
    void testGetUserFromToken() {
        String rawToken = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        DetailedUserDto result = authService.getUserFromToken(rawToken);

        assertThat(result).isEqualTo(detailedUserDto);
        verify(userAuthProvider).validateTokenStrongly("faketoken");
    }
}
