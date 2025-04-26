package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a REST controller for UCSBOrganizations.
 */
@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j
public class UCSBOrganizationsController extends ApiController {

    @Autowired
    UCSBOrganizationsRepository ucsbOrganizationsRepository;

    /**
     * List all UCSB organizations
     * 
     * @return an iterable of UCSBOrganizations
     */
    @Operation(summary = "List all UCSB organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allUCSBOrganizations() {
        Iterable<UCSBOrganizations> organizations = ucsbOrganizationsRepository.findAll();
        return organizations;
    }

    /**
     * Create a new organization
     * 
     * @param orgCode organization code
     * @param orgTranslationShort organization's short translation
     * @param orgTranslation organizations full name
     * @param inactive Is the organization active or inactive?
     * @return the saved organization
     */
    @Operation(summary = "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postUCSBOrganization(
            @Parameter(name = "orgCode") @RequestParam String orgCode,
            @Parameter(name = "orgTranslationShort") @RequestParam String orgTranslationShort,
            @Parameter(name = "orgTranslation") @RequestParam String orgTranslation,
            @Parameter(name = "inactive") @RequestParam boolean inactive)
            throws JsonProcessingException {

        UCSBOrganizations org = new UCSBOrganizations();
        org.setOrgCode(orgCode);
        org.setOrgTranslationShort(orgTranslationShort);
        org.setOrgTranslation(orgTranslation);
        org.setInactive(Boolean.valueOf(inactive));  
        UCSBOrganizations savedOrg = ucsbOrganizationsRepository.save(org);

        return savedOrg;
    }
    /**
     * Get a single organization by id
     * 
     * @param id the id of the organization
     * @return a UCSBOrganization
     */
    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getByCode(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBOrganizations ucsbOrganization = ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));

        return ucsbOrganization;
    }

        /**
     * Update a single organization
     * 
     * @param id       id of the organization to update
     * @param incoming the new organization
     * @return the updated organization object
     */
    @Operation(summary= "Update a organization date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateUCSBOrganization(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBOrganizations incoming) {

        UCSBOrganizations ucsbOrganizations = ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));

        ucsbOrganizations.setOrgCode(incoming.getOrgCode());
        ucsbOrganizations.setOrgTranslation(incoming.getOrgTranslation());
        ucsbOrganizations.setOrgTranslationShort(incoming.getOrgTranslationShort());
        ucsbOrganizations.setInactive(incoming.getInactive());

        ucsbOrganizationsRepository.save(ucsbOrganizations);
        
        return ucsbOrganizations;
    }
        /**
     * Delete a UCSBOrganization
     * 
     * @param id the id of the organization to delete
     * @return a message indicating the organization was deleted
     */
    @Operation(summary= "Delete a UCSBOrganization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBOrganization(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBOrganizations ucsbOrganizations = ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));

        ucsbOrganizationsRepository.delete(ucsbOrganizations);
        return genericMessage("UCSBOrganization with id %s deleted".formatted(id));
    }


}