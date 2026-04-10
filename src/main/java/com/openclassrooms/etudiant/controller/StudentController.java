package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.StudentCreateDto;
import com.openclassrooms.etudiant.dto.StudentDto;
import com.openclassrooms.etudiant.dto.StudentUpdateDto;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        List<StudentDto> students = studentDtoMapper.toDto(studentService.getAllStudents());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    private ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return studentService.getStudentByIdOptional(id)
            .map(student -> ResponseEntity.ok(studentDtoMapper.toDto(student)))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    private ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentCreateDto studentCreateDto) {
        Student registeredStudent = studentService.createStudent(studentDtoMapper.toEntity(studentCreateDto));
        return ResponseEntity.ok(studentDtoMapper.toDto(registeredStudent));
    }

    @PutMapping("/{id}")
    private ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateDto studentUpdateDto) {
        studentUpdateDto.setId(id);
        Student updatedStudent = studentService.updateStudent(studentDtoMapper.toEntity(studentUpdateDto));
        return ResponseEntity.ok(studentDtoMapper.toDto(updatedStudent));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
