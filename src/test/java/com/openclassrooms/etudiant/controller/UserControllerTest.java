package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.LoginRequestDTO;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerTest {

    private static final String REGISTER_URL = "/api/register";
    private static final String LOGIN_URL = "/api/login";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";


    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
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
        userRepository.deleteAll();
    }

    @Test
    public void registerUserWithoutRequiredData() throws Exception {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerAlreadyExistUser() throws Exception {
        // GIVEN
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        userService.register(user);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerUserSuccessful() throws Exception {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void loginUserWithoutRequiredData() throws Exception {
        // GIVEN
        LoginRequestDTO loginDto = new LoginRequestDTO();

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginUserWithUnusedLogin() throws Exception {
        // GIVEN
        User registeredUser = new User();
        registeredUser.setFirstName(FIRST_NAME);
        registeredUser.setLastName(LAST_NAME);
        registeredUser.setLogin(LOGIN);
        registeredUser.setPassword(PASSWORD);
        userService.register(registeredUser);

        String fakeLogin = "fakeLogin@example.com";
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setLogin(fakeLogin);
        loginDto.setPassword(PASSWORD);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginUserWithBadPassword() throws Exception {
        // GIVEN
        User registeredUser = new User();
        registeredUser.setFirstName(FIRST_NAME);
        registeredUser.setLastName(LAST_NAME);
        registeredUser.setLogin(LOGIN);
        registeredUser.setPassword(PASSWORD);
        userService.register(registeredUser);

        String badPassword = "fakePasswordSecure";
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setLogin(LOGIN);
        loginDto.setPassword(badPassword);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginUserSuccessful() throws Exception {
        // GIVEN
        User registeredUser = new User();
        registeredUser.setFirstName(FIRST_NAME);
        registeredUser.setLastName(LAST_NAME);
        registeredUser.setLogin(LOGIN);
        registeredUser.setPassword(PASSWORD);
        userService.register(registeredUser);

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setLogin(LOGIN);
        loginDto.setPassword(PASSWORD);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }
}
