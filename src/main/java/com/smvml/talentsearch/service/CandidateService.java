package com.smvml.talentsearch.service;

import com.smvml.talentsearch.entity.Candidate;
import com.smvml.talentsearch.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;

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
    public double getAverageSalary() {

        List<Candidate> candidates = repository.findAll();

        if(candidates.isEmpty()) {
            return 0;
        }

        double totalSalary = candidates.stream()
                .mapToDouble(Candidate::getExpectedSalary)
                .sum();

        return totalSalary / candidates.size();
    }
    public Double getHighestSalary() {

        return repository.findAll()
                .stream()
                .mapToDouble(Candidate::getExpectedSalary)
                .max()
                .orElse(0);
    }
    public Map<String, Long> getLocationDistribution() {

        return repository.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Candidate::getLocation,
                                Collectors.counting()
                        )
                );
    }

    public Double getLowestSalary() {

        return repository.findAll()
                .stream()
                .mapToDouble(Candidate::getExpectedSalary)
                .min()
                .orElse(0);
    }


    public long getLocationCount() {

        return repository.findAll()
                .stream()
                .map(Candidate::getLocation)
                .distinct()
                .count();
    }
    public Map<String, Long> getSkillDistribution() {

        Map<String, Long> result = new HashMap<>();

        List<Candidate> candidates = repository.findAll();

        for (Candidate c : candidates) {

            String[] skills = c.getSkills().split(",");

            for (String skill : skills) {

                skill = skill.trim();

                result.put(
                    skill,
                    result.getOrDefault(skill, 0L) + 1
                );
            }
        }

        return result;
    }
    public long getCandidateCount(){
        return repository.count();
    }

    public void deleteCandidate(Long id) {
        repository.deleteById(id);
    }
    public Candidate getCandidateById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Candidate Not Found"));
    }
    public List<Candidate> filterCandidates(
            String skill,
            String location,
            Integer exp) {

        List<Candidate> candidates = repository.findAll();

        if (skill != null && !skill.isEmpty()) {
            candidates = candidates.stream()
                    .filter(c ->
                            c.getSkills().toLowerCase()
                                    .contains(skill.toLowerCase()))
                    .toList();
        }

        if (location != null && !location.isEmpty()) {
            candidates = candidates.stream()
                    .filter(c ->
                            c.getLocation().toLowerCase()
                                    .contains(location.toLowerCase()))
                    .toList();
        }

        if (exp != null) {
            candidates = candidates.stream()
                    .filter(c ->
                            c.getExperienceYears() >= exp)
                    .toList();
        }

        return candidates;
    }
}
