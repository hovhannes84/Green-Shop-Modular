package com.example.greenshopweb.controller;

import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopweb.security.CurrentUser;
import com.example.greenshopweb.service.RatingsreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ratingsreviews")
@RequiredArgsConstructor
@Slf4j
public class RatingsreviewController {

    private final RatingsreviewService ratingsreviewService;

    // Method to handle the creation of a new rating and review
    @PostMapping("/create")
    public String createRatingsreview(@ModelAttribute("ratingsreview") CreateRatingsreviewRequestDto createRatingsreviewRequestDto,
                                      @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Creating new rating and review");
        ratingsreviewService.createReviewAndRating(createRatingsreviewRequestDto, currentUser);
        log.info("New rating and review created successfully");
        return "redirect:/products";
    }

    // Method to handle the updating of an existing rating and review
    @PostMapping("/update/{id}")
    public String updateRatingsreview(@PathVariable int id,
                                      @ModelAttribute("ratingsreview") UpdateRatingsreviewRequestDto updatedRatingsreview,
                                      @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Updating rating and review with ID: {}", id);
        ratingsreviewService.updateRatingsreview(updatedRatingsreview, currentUser);
        log.info("Rating and review updated successfully");
        return "redirect:/products";
    }

    // Method to handle the deletion of an existing rating and review
    @PostMapping("/delete/{id}")
    public String deleteRatingsreview(@PathVariable int id) {
        log.info("Deleting rating and review with ID: {}", id);
        ratingsreviewService.deleteRatingsreview(id);
        log.info("Rating and review deleted successfully");
        return "redirect:/products";
    }
}
