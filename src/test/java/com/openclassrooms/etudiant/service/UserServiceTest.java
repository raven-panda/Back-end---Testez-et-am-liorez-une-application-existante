package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    private static final Long ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String UPDATED_FIRST_NAME = "Jeanne";
    private static final String UPDATED_LAST_NAME = "Updated";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
    private static final String FAKE_TOKEN = "fake-token";
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @Test
    public void test_create_null_user_throws_IllegalArgumentException() {
        // GIVEN

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.register(null));
    }

    @Test
    public void test_create_already_exist_user_throws_IllegalArgumentException() {
        // GIVEN
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.register(user));
    }

    @Test
    public void test_create_user() {
        // GIVEN
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        // WHEN
        userService.register(user);

        // THEN
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void test_updateUser_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.updateUser(null));
    }

    @Test
    public void test_updateUserWithNullId_throws_IllegalArgumentException() {
        // GIVEN
        User user = new User();
        user.setId(null);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.updateUser(user));
    }

    @Test
    public void test_updateUserWithNonExistingId_throws_IllegalArgumentException() {
        // GIVEN
        User user = new User();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.updateUser(user));
    }

    @Test
    public void test_updateUser() {
        // GIVEN
        User existingUser = new User();
        existingUser.setId(ID);
        existingUser.setFirstName(FIRST_NAME);
        existingUser.setLastName(LAST_NAME);
        existingUser.setLogin(LOGIN);
        existingUser.setPassword(PASSWORD);

        User userToUpdate = new User();
        userToUpdate.setId(ID);
        userToUpdate.setFirstName(UPDATED_FIRST_NAME);
        userToUpdate.setLastName(UPDATED_LAST_NAME);

        when(userRepository.findById(ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        User result = userService.updateUser(userToUpdate);

        // THEN
        verify(userRepository).save(existingUser);
        assertThat(result.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    public void test_deleteUserWithNullId_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.deleteUser(null));
    }

    @Test
    public void test_deleteUser() {
        // GIVEN
        User existingUser = new User();
        existingUser.setId(ID);
        existingUser.setFirstName(FIRST_NAME);
        existingUser.setLastName(LAST_NAME);
        existingUser.setLogin(LOGIN);
        existingUser.setPassword(PASSWORD);
        when(userRepository.findById(ID)).thenReturn(Optional.of(existingUser));

        // WHEN
        userService.deleteUser(ID);

        // THEN
        verify(userRepository).delete(existingUser);
    }

    @Test
    public void test_loginWithNullLoginOrPassword_throws_IllegalArgumentException() {
        // GIVEN
        String login = null;
        String password = null;

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.login(login, "secure123"));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.login("login@example.com", password));
    }

    @Test
    public void test_loginWithUnusedLoginOrBadPassword_throws_IllegalArgumentException() {
        // GIVEN
        User existingUser = new User();
        existingUser.setId(ID);
        existingUser.setFirstName(FIRST_NAME);
        existingUser.setLastName(LAST_NAME);
        existingUser.setLogin(LOGIN);
        existingUser.setPassword(PASSWORD);
        when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);

        String badLogin = "badlogin@test.io";
        String badPassword = "bad_password123";

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.login(badLogin, PASSWORD));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> userService.login(LOGIN, badPassword));
    }

    @Test
    public void test_login() {
        // GIVEN
        User existingUser = new User();
        existingUser.setId(ID);
        existingUser.setFirstName(FIRST_NAME);
        existingUser.setLastName(LAST_NAME);
        existingUser.setLogin(LOGIN);
        existingUser.setPassword(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn(FAKE_TOKEN);

        // WHEN
        String result = userService.login(LOGIN, PASSWORD);

        // THEN
        Assertions.assertTrue(StringUtils.isNotBlank(result), "Access token is null or empty");
    }
}
