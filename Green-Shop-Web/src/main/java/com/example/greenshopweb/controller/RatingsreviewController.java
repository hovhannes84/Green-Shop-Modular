package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopweb.service.RatingsreviewService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ratingsreviews")
@RequiredArgsConstructor
public class RatingsreviewController {

    private final RatingsreviewService ratingsreviewService;

    @PostMapping("/create")
    public String createRatingsreview(@ModelAttribute("ratingsreview") CreateRatingsreviewRequestDto createRatingsreviewRequestDto,
                                      @AuthenticationPrincipal CurrentUser currentUser) {
        ratingsreviewService.createReviewAndRating(createRatingsreviewRequestDto, currentUser);
        return "redirect:/products";
    }

    @PostMapping("/update/{id}")
    public String updateRatingsreview(@PathVariable int id,
                                      @ModelAttribute("ratingsreview") UpdateRatingsreviewRequestDto updatedRatingsreview,
                                      @AuthenticationPrincipal CurrentUser currentUser) {
        ratingsreviewService.updateRatingsreview(updatedRatingsreview, currentUser);
        return "redirect:/products" ;
    }

    @PostMapping("/delete/{id}")
    public String deleteRatingsreview(@PathVariable int id) {
        ratingsreviewService.deleteRatingsreview(id);
        return "redirect:/products";
    }
}
