package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.example.controllers.ApiController;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;


/**
 * This is a REST controller for MenuItemReview
 */
@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j

public class MenuItemReviewController extends ApiController {
    
    @Autowired //automatically creates a new instance of MenuItemReviewRepository with the instance defined
    MenuItemReviewRepository menuItemReviewRepository;

    /**
     * List all MenuItemReview
     * 
     * @return an iterable of MenuItemReview
     */
    @Operation(summary= "List all menu item reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReview> allMenuItemReviews() {
        Iterable<MenuItemReview> reviews = menuItemReviewRepository.findAll();
        return reviews;
    }

    /**
     * Create a new MenuItemReview
     *  
     * @param itemId the MenuItemReview to create
     * @param reviewerEmail the email of the reviewer
     * @param stars the number of stars given by the reviewer
     * @param comments the comments given by the reviewer
     * @param dateReviewed the date the review was created
     * @return the created MenuItemReview
     */
    @Operation(summary= "Create a new menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReview postMenuItemReviewController(
            @Parameter(name="itemId") @RequestParam long itemId,
            @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="comments") @RequestParam String comments,
            @Parameter(name="dateReviewed", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("dateReviewed={}", dateReviewed);

        MenuItemReview menuitemreview = new MenuItemReview();
        menuitemreview.setItemId(itemId);
        menuitemreview.setReviewerEmail(reviewerEmail);
        menuitemreview.setStars(stars);
        menuitemreview.setComments(comments);
        menuitemreview.setDateReviewed(dateReviewed);

        MenuItemReview savedmenuitemreview = menuItemReviewRepository.save(menuitemreview);

        return savedmenuitemreview;
    }

    /**
     * Get a single MenuItemReview by id
     * 
     * @param id the id of the MenuItemReview
     * @return a MenuItemReview
     */
    @Operation(summary= "Get a single menu item review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReview getById(
            @Parameter(name="id") @RequestParam Long id) {
        MenuItemReview menuitemreview = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        return menuitemreview;
    }

    /**
     * Update a single menu item review
     * 
     * @param id       id of the menu item review to update
     * @param incoming the new menu item review
     * @return the updated menu item review
     * 
     */
    @Operation(summary= "Update a single menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReview updateMenuItemReview(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid MenuItemReview incoming) {

        MenuItemReview menuItemReview = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        menuItemReview.setItemId(incoming.getItemId());
        menuItemReview.setReviewerEmail(incoming.getReviewerEmail());
        menuItemReview.setStars(incoming.getStars());
        menuItemReview.setComments(incoming.getComments());
        menuItemReview.setDateReviewed(incoming.getDateReviewed());

        menuItemReviewRepository.save(menuItemReview);

        return menuItemReview;
    }

    /**
     * Delete a MenuItemReview
     * 
     * @param id the id of the menu item review to delete
     * @return a message indicating the menu item review was deleted
     */
    @Operation(summary= "Delete a MenuItemReview")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteMenuItemReview(
            @Parameter(name="id") @RequestParam Long id) {
        MenuItemReview menuitemreview = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        menuItemReviewRepository.delete(menuitemreview);
        return genericMessage("MenuItemReview with id %s deleted".formatted(id));
    }


    
    
}