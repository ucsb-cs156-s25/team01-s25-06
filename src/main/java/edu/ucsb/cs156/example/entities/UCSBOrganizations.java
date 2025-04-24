package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * This is a JPA entity that represents a UCSBOrganization Data Base
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsborganizations")
public class UCSBOrganizations {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String orgCode;
  private String orgTranslationShort;
  private String orgTranslation;
  private boolean inactive;
}