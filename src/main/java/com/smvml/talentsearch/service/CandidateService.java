package com.smvml.talentsearch.service;

import com.smvml.talentsearch.entity.Candidate;
import com.smvml.talentsearch.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository repository;

    public Candidate saveCandidate(Candidate candidate) {
        return repository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {
        return repository.findAll();
    }
    public List<Candidate> searchBySkill(String skill){
        return repository.findBySkillsContainingIgnoreCase(skill);
    }
    public List<Candidate> searchByLocation(String location) {
        return repository.findByLocationContainingIgnoreCase(location);
    }
    public List<Candidate> searchByExperience(Integer exp){
        return repository.findByExperienceYearsGreaterThanEqual(exp);
    }
    public List<Candidate> searchBySalary(Double salary){
        return repository.findByExpectedSalaryLessThanEqual(salary);
    }
    public long getCandidateCount(){
        return repository.count();
    }
}