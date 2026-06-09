package com.smvml.talentsearch.repository;

import com.smvml.talentsearch.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findBySkillsContainingIgnoreCase(String skills);
    List<Candidate> findByLocationContainingIgnoreCase(String location);
    List<Candidate> findByExperienceYearsGreaterThanEqual(Integer experienceYears);
    List<Candidate> findByExpectedSalaryLessThanEqual(Double salary);
    long count();
    

}