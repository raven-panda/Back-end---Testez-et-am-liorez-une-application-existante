package com.openclassrooms.etudiant.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorDetails extends ErrorDetails {
    Map<String, String> errors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message, String details, Map<String, String> errors) {
        super(timestamp, message, details);
        this.errors = errors;
    }
}
