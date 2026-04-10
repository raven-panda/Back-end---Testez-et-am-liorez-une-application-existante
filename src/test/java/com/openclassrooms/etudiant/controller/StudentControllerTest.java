package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.dto.StudentCreateDto;
import com.openclassrooms.etudiant.dto.StudentUpdateDto;
import com.openclassrooms.etudiant.dto.UserDto;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser(username = "login")
public class StudentControllerTest {

    private static final String URL = "/api/student";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String UPDATED_FIRST_NAME = "Jeanne";
    private static final String UPDATED_LAST_NAME = "Updated";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String MVC_LOGIN = "mvclogin";
    private static final String MVC_PASSWORD = "mvcpassword";

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getAllStudentsEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void getAllStudentsNonEmptyList() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        // Use a random UUID along login to avoid conflict between tests
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        studentService.createStudent(student);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void getStudentByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getStudentByIdSuccessful() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        Student createdStudent = studentService.createStudent(student);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + createdStudent.getId())
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdStudent.getId()));
    }

    @Test
    public void createStudentWithoutRequiredData() throws Exception {
        // GIVEN
        StudentCreateDto studentCreateDto = new StudentCreateDto();

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .content(objectMapper.writeValueAsString(studentCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createStudentSuccessful() throws Exception {
        // GIVEN
        StudentCreateDto studentCreateDto = new StudentCreateDto();
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN + UUID.randomUUID());
        registerDTO.setPassword(PASSWORD);
        studentCreateDto.setUser(registerDTO);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .content(objectMapper.writeValueAsString(studentCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(FIRST_NAME))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(LAST_NAME));
    }

    @Test
    public void updateStudentWithoutRequiredData() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        Student createdStudent = studentService.createStudent(student);

        StudentUpdateDto studentCreateDto = new StudentUpdateDto();
        studentCreateDto.setId(createdStudent.getId());

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + studentCreateDto.getId())
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .content(objectMapper.writeValueAsString(studentCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateStudentWithUnusedId() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        studentService.createStudent(student);

        StudentUpdateDto studentCreateDto = new StudentUpdateDto();
        studentCreateDto.setId(400L);
        UserDto studentUpdatedUser = new UserDto();
        studentUpdatedUser.setId(studentUser.getId());
        studentUpdatedUser.setFirstName(UPDATED_FIRST_NAME);
        studentUpdatedUser.setLastName(UPDATED_LAST_NAME);
        studentCreateDto.setUser(studentUpdatedUser);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + studentCreateDto.getId())
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .content(objectMapper.writeValueAsString(studentCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateStudentSuccessful() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        Student createdStudent = studentService.createStudent(student);

        StudentUpdateDto studentCreateDto = new StudentUpdateDto();
        studentCreateDto.setId(createdStudent.getId());
        UserDto studentUpdatedUser = new UserDto();
        studentUpdatedUser.setId(studentUser.getId());
        studentUpdatedUser.setFirstName(UPDATED_FIRST_NAME);
        studentUpdatedUser.setLastName(UPDATED_LAST_NAME);
        studentCreateDto.setUser(studentUpdatedUser);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + studentCreateDto.getId())
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .content(objectMapper.writeValueAsString(studentCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(studentCreateDto.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(studentCreateDto.getUser().getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(UPDATED_FIRST_NAME))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(UPDATED_LAST_NAME));
    }

    @Test
    public void deleteStudentWithUnusedId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/400")
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteStudentSuccessful() throws Exception {
        // GIVEN
        Student student = new Student();
        User studentUser = new User();
        studentUser.setFirstName(FIRST_NAME);
        studentUser.setLastName(LAST_NAME);
        studentUser.setLogin(LOGIN + UUID.randomUUID());
        studentUser.setPassword(PASSWORD);
        student.setUser(studentUser);
        Student createdStudent = studentService.createStudent(student);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + createdStudent.getId())
                //.header("Authorization", "Bearer " + authenticateAndGetToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}
