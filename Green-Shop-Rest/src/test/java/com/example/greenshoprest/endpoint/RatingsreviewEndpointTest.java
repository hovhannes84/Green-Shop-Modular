package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.RatingsreviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RatingsreviewEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingsreviewService ratingsreviewService;


    @Test
    void testCreateRatingsreview() throws Exception {
        int ratingsreviewId = 1;
        UpdateRatingsreviewRequestDto requestDto = new UpdateRatingsreviewRequestDto();

        mockMvc.perform(post("/ratingsreviews/update/{id}", ratingsreviewId)
                        .flashAttr("ratingsreview", requestDto))
                .andExpect(status().isOk());

        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getUsername()).thenReturn("poxos");

        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        when(ratingsreviewService.createReviewAndRating(any(), any())).thenReturn(ResponseEntity.ok(null));
        RatingsreviewEndpoint endpoint = new RatingsreviewEndpoint(ratingsreviewService);

        ResponseEntity<?> actualResponse = endpoint.createRatingsreview(CreateRatingsreviewRequestDto.builder().build(), currentUser);
        verify(ratingsreviewService).createReviewAndRating(CreateRatingsreviewRequestDto.builder().build(), currentUser);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }

    @Test
    @WithMockUser("poxos")
    public void testDeleteRatingsreview() throws Exception {
        int ratingsreviewId = 1;
        when(ratingsreviewService.deleteRatingsreview(ratingsreviewId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/ratingsreviews/delete/{id}", ratingsreviewId))
                .andExpect(status().isOk());
        verify(ratingsreviewService, times(1)).deleteRatingsreview(ratingsreviewId);
    }
}