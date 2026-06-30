package com.smvml.talentsearch.security;

import com.smvml.talentsearch.entity.Candidate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OwnershipValidator {

    /**
     * Validates that the logged-in user's email matches the candidate's email.
     * Ownership Rule: candidate.email == loggedInUser.email
     *
     * Throws 403 FORBIDDEN if the requester does not own the candidate profile.
     */
    public void validateOwnership(Candidate candidate, String loggedInEmail) {

        if (loggedInEmail == null || loggedInEmail.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Missing requester identity. Access denied."
            );
        }

        if (candidate == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidate not found."
            );
        }

        if (candidate.getEmail() == null ||
                !candidate.getEmail().equalsIgnoreCase(loggedInEmail.trim())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own profile."
            );
        }
    }

    /**
     * Returns true/false instead of throwing — used by read-only
     * "canEdit" style checks where a boolean is more convenient
     * than an exception (e.g. for non-mutating pre-checks).
     */
    public boolean isOwner(Candidate candidate, String loggedInEmail) {

        if (candidate == null ||
                candidate.getEmail() == null ||
                loggedInEmail == null) {
            return false;
        }

        return candidate.getEmail().equalsIgnoreCase(loggedInEmail.trim());
    }
}
