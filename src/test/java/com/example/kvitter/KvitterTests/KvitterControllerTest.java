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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class KvitterControllerTest {

    @Mock
    private KvitterService kvitterService;

    @Mock
    private RekvittService rekvittService;
    
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
        
        doNothing().when(kvitterService).addKvitter(eq(request), eq(token));

        ResponseEntity<Map<String, String>> response = kvitterController.postKvitter(request, token);

        verify(kvitterService).addKvitter(eq(request), eq(token));
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Kvitter posted!");
    }

    @Test
    void testRemoveKvitter() {
        RemoveKvitterRequest request = new RemoveKvitterRequest(UUID.randomUUID().toString());
        String token = "Bearer faketoken";
        

        ResponseEntity<Map<String, String>> response = kvitterController.removeKvitter(request, token);

        verify(kvitterService).removeKvitter(eq(request.id()), eq(token));
        assertThat(response.getBody()).containsEntry("message", "Kvitter deleted!");
    }

    @Test
    void testGetSearchedKvitterDtoList() {
        String token = "Bearer faketoken";
        List<DetailedDtoInterface> mockResult = new ArrayList<>();

        when(kvitterService.getSearchedKvitters("hashtag", "test", token)).thenReturn(mockResult);

        List<DetailedDtoInterface> result = kvitterController.getSearchedKvitterDtoList("hashtag", "test", token);

        assertThat(result).isEqualTo(mockResult);
        verify(kvitterService).getSearchedKvitters("hashtag", "test", token);
    }

    @Test
    void testGetSearchedKvitterDtoList_withUnknownCategory() {
        String token = "Bearer faketoken";

        when(kvitterService.getSearchedKvitters("unknown", "searchterm", token)).thenReturn(List.of());

        List<DetailedDtoInterface> result = kvitterController.getSearchedKvitterDtoList("unknown", "searchterm", token);

        assertThat(result).isEmpty();
        verify(kvitterService).getSearchedKvitters("unknown", "searchterm", token);
    }



    @Test
    void testGetDynamicDetailedKvitterDtoList() {
        String token = "Bearer faketoken";

   
        when(rekvittService.getFilteredRekvitts(anyString(), anyString(), any())).thenReturn(List.of());
        when(kvitterService.getFilteredKvitters(anyString(), anyString(), any())).thenReturn(List.of());

        List<DetailedDtoInterface> result = kvitterController.getDynamicDetailedKvitterDtoList("Following", "testuser", token);

        assertThat(result).isNotNull();
    }



    @Test
    void testGetWelcomePageKvitter() {
        when(kvitterService.getTenLatestKvitterThatIsNotPrivate()).thenReturn(List.of());

        List<DetailedKvitterDto> result = kvitterController.getWelcomePageKvitter();

        assertThat(result).isNotNull();
    }
}

