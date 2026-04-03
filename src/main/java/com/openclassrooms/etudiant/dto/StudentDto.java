package com.openclassrooms.etudiant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentDto {

    private Long id;

    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String firstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
