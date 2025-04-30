package com.example.kvitter.KvitterTests;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.KvitterController;
import com.example.kvitter.dtos.*;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.services.RekvittService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class KvitterControllerTest {

    @Mock
    private KvitterService kvitterService;

    @Mock
    private RekvittService rekvittService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private KvitterController kvitterController;

    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(UUID.randomUUID());
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");
    }

    @Test
    void testPostKvitter() {
        KvitterRequest request = new KvitterRequest("Test message", List.of("tag1", "tag2"), false);
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        kvitterController.postKvitter(request, token);

        verify(kvitterService).addKvitter(eq("Test message"), eq(List.of("tag1", "tag2")), eq(false), eq(detailedUserDto));
    }

    @Test
    void testGetSearchedKvitterDtoList() {
        String token = "Bearer faketoken";

        when(userAuthProvider.validateToken("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);
        when(kvitterService.getSearchedKvitters(anyString(), anyString(), any())).thenReturn(new java.util.ArrayList<>());


        List<DetailedDtoInterface> result = kvitterController.getSearchedKvitterDtoList("hashtag", "test", token);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetDynamicDetailedKvitterDtoList() {
        String token = "Bearer faketoken";

        when(userAuthProvider.validateToken("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);
        when(rekvittService.getFilteredRekvitts(anyString(), anyString(), any())).thenReturn(List.of());
        when(kvitterService.getFilteredKvitters(anyString(), anyString(), any())).thenReturn(List.of());

        List<DetailedDtoInterface> result = kvitterController.getDynamicDetailedKvitterDtoList("Following", "testuser", token);

        assertThat(result).isNotNull();
    }


    @Test
    void testRemoveKvitter() {
        RemoveKvitterRequest request = new RemoveKvitterRequest(UUID.randomUUID().toString());
        String token = "Bearer faketoken";

        kvitterController.removeKvitter(request, token);

        verify(kvitterService).removeKvitter(eq(request.id()));
    }

    @Test
    void testGetWelcomePageKvitter() {
        when(kvitterService.getTenLatestKvitterThatIsNotPrivate()).thenReturn(List.of());

        List<DetailedKvitterDto> result = kvitterController.getWelcomePageKvitter();

        assertThat(result).isNotNull();
    }
}

