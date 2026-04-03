package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.UserDto;
import com.openclassrooms.etudiant.mapper.UserDtoMapper;
import com.openclassrooms.etudiant.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @GetMapping()
    private ResponseEntity<List<UserDto>> getAllStudent() {
        return ResponseEntity.ok(List.of(new UserDto()));
    }

    @GetMapping("/{id:long}")
    private ResponseEntity<UserDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserDto());
    }

    @PostMapping()
    private ResponseEntity<UserDto> createStudent(@Valid @RequestBody UserDto user) {
        return ResponseEntity.ok(new UserDto());
    }

    @PutMapping("/{id:long}")
    private ResponseEntity<UserDto> updateStudent(@PathVariable Long id, @Valid @RequestBody UserDto user) {
        return ResponseEntity.ok(new UserDto());
    }

    @DeleteMapping("/{id:long}")
    private ResponseEntity<UserDto> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.ok(new UserDto());
    }

}
