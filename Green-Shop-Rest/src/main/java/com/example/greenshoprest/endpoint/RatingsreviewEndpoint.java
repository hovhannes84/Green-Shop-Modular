package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.RatingsreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/ratingsreviews")
@RequiredArgsConstructor
@Slf4j
public class RatingsreviewEndpoint {

    private final RatingsreviewService ratingsreviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createRatingsreview(@Valid @ModelAttribute("ratingsreview") CreateRatingsreviewRequestDto createRatingsreviewRequestDto,
                                                 @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Creating a new ratings review");
        return ratingsreviewService.createReviewAndRating(createRatingsreviewRequestDto, currentUser);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRatingsreview(@PathVariable int id,
                                                 @Valid @ModelAttribute("ratingsreview") UpdateRatingsreviewRequestDto updatedRatingsreview,
                                                 @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Updating ratings review with ID: {}", id);
        return ratingsreviewService.updateRatingsreview(updatedRatingsreview, currentUser);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteRatingsreview(@PathVariable int id) {
        log.info("Deleting ratings review with ID: {}", id);
        return ratingsreviewService.deleteRatingsreview(id);
    }

}
