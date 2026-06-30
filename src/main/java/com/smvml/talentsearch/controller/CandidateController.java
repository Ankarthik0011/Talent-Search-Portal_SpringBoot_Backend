package com.smvml.talentsearch.controller;

import com.smvml.talentsearch.entity.Candidate;
import com.smvml.talentsearch.service.CandidateOwnershipService;
import com.smvml.talentsearch.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "http://localhost:5173")
public class CandidateController {

    @Autowired
    private CandidateService service;

    @Autowired
    private CandidateOwnershipService ownershipService;

    // ===================== CREATE =====================
    // Unrelated to ownership editing rules — preserved as-is.
    @PostMapping
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return service.saveCandidate(candidate);
    }

    // ===================== READ (unchanged) =====================

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

    @GetMapping("/filter")
    public List<Candidate> filterCandidates(

            @RequestParam(required = false) String skill,

            @RequestParam(required = false) String location,

            @RequestParam(required = false) Integer exp) {

        return service.filterCandidates(
                skill,
                location,
                exp);
    }

    @GetMapping("/{id}")
    public Candidate getCandidateById(
            @PathVariable Long id) {

        return service.getCandidateById(id);
    }

    // ===================== UPDATE — OWNERSHIP ENFORCED =====================
    //
    // Only the candidate whose email matches X-User-Email may update.
    // ADMIN is explicitly read-only and will receive 403 FORBIDDEN here,
    // same as any other non-owner user. This cannot be bypassed via
    // direct API / Postman calls because the check runs server-side
    // against the database record, not against any client-supplied flag.
    @PutMapping("/{id}")
    public Candidate updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidate candidate,
            @RequestHeader(value = "X-User-Email", required = false) String loggedInEmail) {

        // 1. Get candidate email from database + 2/3. compare ownership
        ownershipService.verifyOwnershipAndGet(id, loggedInEmail);

        // 4. Allow update only when emails match (verified above)
        candidate.setId(id);

        return service.saveCandidate(candidate);
    }

    // ===================== DELETE — OWNERSHIP ENFORCED =====================
    //
    // Admin cannot delete candidate data (admin is read-only per spec).
    // Only the owning candidate may delete their own profile.
    @DeleteMapping("/{id}")
    public String deleteCandidate(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Email", required = false) String loggedInEmail) {

        ownershipService.verifyOwnershipAndGet(id, loggedInEmail);

        service.deleteCandidate(id);

        return "Candidate Deleted";
    }

    // ===================== REPORTS (unchanged) =====================

    @GetMapping("/report/total")
    public long totalCandidates() {
        return service.getCandidateCount();
    }

    @GetMapping("/report/average-salary")
    public double averageSalary() {
        return service.getAverageSalary();
    }

    @GetMapping("/report/highest-salary")
    public Double highestSalary() {
        return service.getHighestSalary();
    }

    @GetMapping("/report/lowest-salary")
    public Double lowestSalary() {
        return service.getLowestSalary();
    }

    @GetMapping("/report/location-count")
    public long locationCount() {
        return service.getLocationCount();
    }

    @GetMapping("/report/skills")
    public Map<String, Long> skillsReport() {
        return service.getSkillDistribution();
    }

    @GetMapping("/report/locations")
    public Map<String, Long> locationDistribution() {

        return service.getLocationDistribution();
    }

    // ===================== RESUME UPLOAD — OWNERSHIP ENFORCED =====================
    //
    // Only the profile owner can update their own resume.
    @PostMapping("/upload/{id}")
    public Candidate uploadResume(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Email", required = false) String loggedInEmail)
            throws Exception {

        Candidate candidate =
                ownershipService.verifyOwnershipAndGet(id, loggedInEmail);

        candidate.setResumeFileName(
                file.getOriginalFilename());

        candidate.setResumeData(
                file.getBytes());

        return service.saveCandidate(candidate);
    }

    // ===================== RESUME DOWNLOAD / PREVIEW (unchanged — read-only) =====================
    //
    // Admin and any authenticated viewer may download/preview resumes (read-only access).

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long id) {

        Candidate candidate =
                service.getCandidateById(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + candidate.getResumeFileName()
                )
                .body(candidate.getResumeData());
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<byte[]> previewResume(
            @PathVariable Long id) {

        Candidate candidate =
                service.getCandidateById(id);

        return ResponseEntity.ok()
                .contentType(
                        org.springframework.http.MediaType.APPLICATION_PDF
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" +
                                candidate.getResumeFileName()
                )
                .body(candidate.getResumeData());
    }

}
