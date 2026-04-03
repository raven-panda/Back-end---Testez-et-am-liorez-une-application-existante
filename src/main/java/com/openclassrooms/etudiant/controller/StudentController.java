package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.StudentCreateDto;
import com.openclassrooms.etudiant.dto.StudentDto;
import com.openclassrooms.etudiant.dto.UserDto;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;

    @GetMapping()
    private ResponseEntity<List<StudentDto>> getAllStudent() {
        return ResponseEntity.ok(List.of(new StudentDto()));
    }

    @GetMapping("/{id:long}")
    private ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(new StudentDto());
    }

    @PostMapping()
    private ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentCreateDto studentCreateDto) {
        Student studentEntity = studentDtoMapper.toEntity(studentCreateDto);
        Student registeredStudent = studentService.createStudent(studentEntity);
        return ResponseEntity.ok(studentDtoMapper.toDto(registeredStudent));
    }

    @PutMapping("/{id:long}")
    private ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @Valid @RequestBody UserDto user) {
        return ResponseEntity.ok(new StudentDto());
    }

    @DeleteMapping("/{id:long}")
    private ResponseEntity<StudentDto> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.ok(new StudentDto());
    }

}
