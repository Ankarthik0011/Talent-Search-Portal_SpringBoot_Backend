package com.smvml.talentsearch.service;

import com.smvml.talentsearch.entity.Candidate;
import com.smvml.talentsearch.repository.CandidateRepository;
import com.smvml.talentsearch.security.OwnershipValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateOwnershipService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private OwnershipValidator ownershipValidator;

    /**
     * Loads the candidate by id and verifies that loggedInEmail
     * matches candidate.email. Throws 403 FORBIDDEN if not.
     *
     * Used by every mutating endpoint (update, resume upload) so
     * ownership cannot be bypassed via direct API / Postman calls.
     */
    public Candidate verifyOwnershipAndGet(Long candidateId, String loggedInEmail) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Candidate Not Found"));

        ownershipValidator.validateOwnership(candidate, loggedInEmail);

        return candidate;
    }

    /**
     * Convenience boolean check (no exception) — useful for read-only
     * "can this user edit this profile" UI-support endpoints, if needed.
     */
    public boolean isOwner(Long candidateId, String loggedInEmail) {

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);

        return ownershipValidator.isOwner(candidate, loggedInEmail);
    }
}
