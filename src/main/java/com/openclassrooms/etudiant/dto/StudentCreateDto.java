package com.openclassrooms.etudiant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentCreateDto {
    @Valid
    @NotNull
    private RegisterDTO user;
}
