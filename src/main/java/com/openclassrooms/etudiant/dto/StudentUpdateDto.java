package com.openclassrooms.etudiant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentUpdateDto {

    @NotNull
    private Long id;

    @Valid
    @NotNull
    private UserDto user;
}
