package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.RatingsreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratingsreviews")
@RequiredArgsConstructor
public class RatingsreviewEndpoint {

    private final RatingsreviewService ratingsreviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createRatingsreview(@ModelAttribute("ratingsreview") CreateRatingsreviewRequestDto createRatingsreviewRequestDto,
                                              @AuthenticationPrincipal CurrentUser currentUser) {
        return  ratingsreviewService.createReviewAndRating(createRatingsreviewRequestDto, currentUser);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRatingsreview(@PathVariable int id,
                                                   @ModelAttribute("ratingsreview") UpdateRatingsreviewRequestDto updatedRatingsreview,
                                                   @AuthenticationPrincipal CurrentUser currentUser) {
        return  ratingsreviewService.updateRatingsreview(updatedRatingsreview, currentUser);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteRatingsreview(@PathVariable int id) {
        return ratingsreviewService.deleteRatingsreview(id);
    }

}
