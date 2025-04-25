package edu.ucsb.cs156.example.controllers;


import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * This is a REST controller for UCSBDates
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
     * @return an iterable of UCSBOrganization
     */
    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allUCSBDates() {
        Iterable<UCSBOrganizations> organizations = ucsbOrganizationsRepository.findAll();
        return organizations;
    }
//     //  private String orgCode;
//   private String orgTranslationShort;
//   private String orgTranslation;
//   private boolean inactive;
    /**
     * Create a new organization
     * 
     * @param orgCode  The organization code
     * @param orgTranslationShort  The organization's short translation
     * @param orgTranslation Organizations full name
     * @param inactive Is the organization active or inactive?
     * @return the saved organization
     */
    @Operation(summary= "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postUCSBOrganization(
            @Parameter(name="orgCode") @RequestParam String orgCode,
            @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
            @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
            @Parameter(name="inactive") @RequestParam boolean inactive)
            throws JsonProcessingException {


        UCSBOrganizations org = new UCSBOrganizations();
        org.setOrgCode(orgCode);
        org.setOrgTranslationShort(orgTranslationShort);
        org.setOrgTranslation(orgTranslation);
        org.setInactive(inactive);
        UCSBOrganizations savedOrg = ucsbOrganizationsRepository.save(org);

        return savedOrg;
    }

}
