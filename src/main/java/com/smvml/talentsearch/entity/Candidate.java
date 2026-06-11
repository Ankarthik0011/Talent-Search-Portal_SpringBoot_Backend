package com.smvml.talentsearch.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "candidates")
@Data
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private String skills;

    private Integer experienceYears;

    private String location;

    private String currentCompany;

    private Double expectedSalary;

    private Integer noticePeriod;

    // Resume Upload

    private String resumeFileName;

    @Lob
    @Column(name = "resume_data")
    private byte[] resumeData;
}