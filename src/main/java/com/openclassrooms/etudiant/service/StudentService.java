package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserService userService;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }

        return optionalStudent.get();
    }

    public Student createStudent(Student student) {
        User createdUser = userService.register(student.getUser());
        student.setUser(createdUser);
        return studentRepository.save(student);
    }

    public Student updateStudent(Student student) {
        Assert.notNull(student, "Student must not be null");
        Assert.notNull(student.getId(), "Student id must not be null");
        log.info("Updating student with id {}", student.getId());

        Student existingStudent = getStudentById(student.getId());

        existingStudent.setUser(userService.updateUser(student.getUser()));

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {
        log.info("Deleting student with id {}", id);
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}
