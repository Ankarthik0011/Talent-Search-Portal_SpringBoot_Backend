package com.smvml.talentsearch.controller;

import com.smvml.talentsearch.entity.Candidate;
import com.smvml.talentsearch.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin("*")
public class CandidateController {

    @Autowired
    private CandidateService service;

    @PostMapping
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return service.saveCandidate(candidate);
    }

    @GetMapping
    public List<Candidate> getAllCandidates() {
        return service.getAllCandidates();
    }

    @GetMapping("/search")
    public List<Candidate> searchBySkill(@RequestParam String skill) {
        return service.searchBySkill(skill);
    }
    @GetMapping("/location")
    public List<Candidate> searchByLocation(
            @RequestParam String location) {

        return service.searchByLocation(location);
    }
    @GetMapping("/experience")
    public List<Candidate> searchByExperience(
            @RequestParam Integer exp){
        return service.searchByExperience(exp);
    }
    @GetMapping("/salary")
    public List<Candidate> searchBySalary(
            @RequestParam Double salary){
        return service.searchBySalary(salary);
    }
    @GetMapping("/count")
    public long getCount(){
        return service.getCandidateCount();
    }
}