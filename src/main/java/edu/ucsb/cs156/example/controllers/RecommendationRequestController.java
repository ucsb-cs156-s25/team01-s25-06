package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import java.time.LocalDate;

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
 * This is a REST controller for Recommendation Requests COntroller
 */

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository rrrepository;

    /**
     * List all Recommendation Requests
     * 
     * @return an iterable of Recommendation Requests
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        Iterable<RecommendationRequest> rr = rrrepository.findAll();
        return rr;
    }

    /**
     * Get a single recommendation request by id
     * 
     * @param id the id of the recommendation request
     * @return a recommendation request
     */
    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest rr = rrrepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return rr;
    }

    /**
     * Create a new recommendation request
     * 
     * @param requesterEmail  the email of the requester
     * @param professorEmail  the email of the professor
     * @param explanation     the explanation for the request
     * @param dateRequested   the date the request was made
     * @param dateNeeded      the date the request is needed by
     * @param done           whether the request is done
     * @return the saved recommendation request
     */
    @Operation(summary= "Create a recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="professorEmail") @RequestParam String professorEmail,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="dateRequested", description="date (in iso format, e.g. YYYY-mm-dd; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateRequested") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateRequested,
            @Parameter(name="dateNeeded", description="date (in iso format, e.g. YYYY-mm-dd; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateNeeded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNeeded,
            @Parameter(name="done") @RequestParam boolean done)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("dateRequested={}", dateRequested);

        RecommendationRequest rr = new RecommendationRequest();
        rr.setRequesterEmail(requesterEmail);
        rr.setProfessorEmail(professorEmail);
        rr.setExplanation(explanation);
        rr.setDateRequested(dateRequested);
        rr.setDateNeeded(dateNeeded);
        rr.setDone(done);

        RecommendationRequest savedRecommendationRequest = rrrepository.save(rr);

        return savedRecommendationRequest;
    }

    /**
     * Delete a Recommendation Request
     * 
     * @param id the id of the recommendation request to delete
     * @return a message indicating the recommendation request was deleted
     */
    @Operation(summary= "Delete a Recommendation Request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest rr = rrrepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        rrrepository.delete(rr);
        return genericMessage("Recommendation Request with id %s deleted".formatted(id));
    }

    /**
     * Update a single recommendation request
     * 
     * @param id       id of the recommendation request to update
     * @param incoming the new recommendation request 
     * @return the updated recommendation request object
     */
    @Operation(summary= "Update a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest rr = rrrepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        rr.setRequesterEmail(incoming.getRequesterEmail());
        rr.setProfessorEmail(incoming.getProfessorEmail());
        rr.setExplanation(incoming.getExplanation());
        rr.setDateRequested(incoming.getDateRequested());
        rr.setDateNeeded(incoming.getDateNeeded());
        rr.setDone(incoming.getDone());


        rrrepository.save(rr);

        return rr;
    }
}
