package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {
    private static final Long ID = 1L;
    private static final Long USER_ID = 2L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String UPDATED_FIRST_NAME = "Jeanne";
    private static final String UPDATED_LAST_NAME = "Updated";
    @Mock
    private UserService userService;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentService studentService;

    @Test
    public void test_getAllStudents_empty() {
        // GIVEN
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());

        // WHEN
        List<Student> studentList = studentService.getAllStudents();

        // THEN
        verify(studentRepository).findAll();
        assertThat(studentList).isEmpty();
    }

    @Test
    public void test_getAllStudents_nonEmpty() {
        // GIVEN
        Student existingStudent = new Student();
        existingStudent.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        existingStudent.setUser(existingStudentUser);
        when(studentRepository.findAll()).thenReturn(List.of(existingStudent));

        // WHEN
        List<Student> studentList = studentService.getAllStudents();

        // THEN
        verify(studentRepository).findAll();
        assertThat(studentList).contains(existingStudent);
    }

    @Test
    public void test_getStudentByIdWithUnusedId_throws_IllegalArgumentException() {
        // GIVEN
        Long fakeId = 15L;

        Student existingStudent = new Student();
        existingStudent.setId(fakeId);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        existingStudent.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.getStudentById(fakeId));
    }

    @Test
    public void test_getStudentById() {
        // GIVEN
        Student existingStudent = new Student();
        existingStudent.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        existingStudent.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));

        // WHEN
        Student foundStudent = studentService.getStudentById(ID);

        // THEN
        verify(studentRepository).findById(ID);
        assertThat(foundStudent).isEqualTo(existingStudent);
    }

    @Test
    public void test_createStudentNull_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.createStudent(null));
    }

    @Test
    public void test_createStudentNullUser_throws_IllegalArgumentException() {
        // GIVEN
        Student student = new Student();
        student.setUser(null);
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.createStudent(student));
    }

    @Test
    public void test_createStudent() {
        // GIVEN
        Student student = new Student();
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        student.setUser(existingStudentUser);
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        studentService.createStudent(student);

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(student);
    }

    @Test
    public void test_updateStudentNull_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.updateStudent(null));
    }

    @Test
    public void test_updateStudentNullId_throws_IllegalArgumentException() {
        // GIVEN
        Student student = new Student();
        student.setId(null);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        student.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(student));

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.updateStudent(student));
    }

    @Test
    public void test_updateStudentUnusedId_throws_IllegalArgumentException() {
        // GIVEN
        Long fakeId = 15L;

        Student student = new Student();
        student.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        student.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(student));

        Student studentToUpdate = new Student();
        studentToUpdate.setId(fakeId);
        User studentToUpdateUser = new User();
        studentToUpdateUser.setId(USER_ID);
        studentToUpdateUser.setFirstName(FIRST_NAME);
        studentToUpdateUser.setLastName(LAST_NAME);
        studentToUpdate.setUser(studentToUpdateUser);

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.updateStudent(studentToUpdate));
    }

    @Test
    public void test_updateStudent() {
        // GIVEN
        Student student = new Student();
        student.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        student.setUser(existingStudentUser);
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        Student studentToUpdate = new Student();
        studentToUpdate.setId(ID);
        User studentToUpdateUser = new User();
        studentToUpdateUser.setId(USER_ID);
        studentToUpdateUser.setFirstName(FIRST_NAME);
        studentToUpdateUser.setLastName(LAST_NAME);
        studentToUpdate.setUser(studentToUpdateUser);
        when(userService.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        studentService.updateStudent(studentToUpdate);

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(studentToUpdate);
    }

    @Test
    public void test_deleteStudentNull_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.deleteStudent(null));
    }

    @Test
    public void test_deleteStudentUnusedId_throws_IllegalArgumentException() {
        // GIVEN
        Long fakeId = 15L;

        Student existingStudent = new Student();
        existingStudent.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        existingStudent.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> studentService.deleteStudent(fakeId));
    }

    @Test
    public void test_deleteStudent() {
        // GIVEN
        Student existingStudent = new Student();
        existingStudent.setId(ID);
        User existingStudentUser = new User();
        existingStudentUser.setId(USER_ID);
        existingStudentUser.setFirstName(FIRST_NAME);
        existingStudentUser.setLastName(LAST_NAME);
        existingStudent.setUser(existingStudentUser);
        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));

        // WHEN
        studentService.deleteStudent(ID);

        // THEN
        verify(studentRepository).delete(existingStudent);
        verify(userService).deleteUser(existingStudentUser.getId());
    }
}
