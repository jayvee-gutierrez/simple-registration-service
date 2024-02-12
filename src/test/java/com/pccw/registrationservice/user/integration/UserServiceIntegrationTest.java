package com.pccw.registrationservice.user.integration;

import com.pccw.registrationservice.notification.SimpleMailService;
import com.pccw.registrationservice.notification.dto.SimpleMail;
import com.pccw.registrationservice.user.UserRepository;
import com.pccw.registrationservice.user.UserService;
import com.pccw.registrationservice.user.dto.UpdateUserDto;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import com.pccw.registrationservice.user.exception.DuplicateUsernameOrEmailException;
import com.pccw.registrationservice.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private static SimpleMailService mockMailService;

    private static UserDto validUser1;

    private static UserDto validUser2;

    @BeforeAll
    public static void init() {
        validUser1 = new UserDto();
        validUser1.setUsername("user1");
        validUser1.setEmail("user1@gmail.com");
        validUser1.setPassword("P@ssw0rd");
        validUser1.setFirstName("John");
        validUser1.setLastName("Doe");

        validUser2 = new UserDto();
        validUser2.setUsername("user2");
        validUser2.setEmail("user2@gmail.com");
        validUser2.setPassword("P@ssw0rd");
        validUser2.setFirstName("Jay");
        validUser2.setLastName("Weed");
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    public void whenCreateUser_thenReturnUserResponse() {
        UserResponse response = userService.createUser(validUser1);
        assertNewUsersAgainstUserDtoRequest(validUser1, response);

        // assert email sending
        ArgumentCaptor<SimpleMail> captor = ArgumentCaptor.captor();
        verify(mockMailService, times(1)).sendSimpleEmail(captor.capture());
        SimpleMail captured = captor.getValue();
        assertEquals("Registration Confirmed - Test", captured.getSubject());
        assertEquals(
                String.format("Congratulations! You are successfully registered to PCCW. Your username is: %s", response.getUsername()),
                captured.getBody());
        assertEquals(response.getEmail(), captured.getMailTo());

    }

    @Test
    public void whenCreateUser_andUsernameOrEmailAlreadyExists_thenThrowDuplicateUsernameOrEmailException() {
        UserResponse response = userService.createUser(validUser1);
        assertNotNull(response);
        // create a new user using the same request
        assertThrows(DuplicateUsernameOrEmailException.class, () -> userService.createUser(validUser1));
    }

    @Test
    public void whenGetUserById_thenReturnUserResponse() {
        UserResponse createdUser = userService.createUser(validUser1);

        UserResponse user = userService.getUser(createdUser.getId());
        assertNewUsersAgainstUserDtoRequest(validUser1, user);
    }

    @Test
    public void whenGetUserById_andUserDoesNotExist_thenThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1));
    }

    @Test
    public void whenGetAllUsers_thenReturnAllUsers() {
        List<UserResponse> createdUsers = createUsers();
        List<UserResponse> users = userService.getAllUsers();
        assertEquals(2, users.size());

        Optional<UserResponse> userResponse1 = users.stream().filter(u -> "user1".equals(u.getUsername())).findFirst();
        assertTrue(userResponse1.isPresent());
        assertNewUsersAgainstUserDtoRequest(validUser1, userResponse1.get());

        Optional<UserResponse> userResponse2 = users.stream().filter(u -> "user2".equals(u.getUsername())).findFirst();
        assertTrue(userResponse2.isPresent());
        assertNewUsersAgainstUserDtoRequest(validUser2, userResponse2.get());
    }

    @Test
    public void whenGetAllUsers_andThereAreNoUsers_thenReturnEmptyList() {
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    public void whenUpdateUsers_thenReturnUpdatedUsers() throws InterruptedException {
        List<UserResponse> createdUsers = createUsers();
        // update users' usernames
        List<UpdateUserDto> updateUserDtos = createdUsers.stream()
                .map(u -> {
                    UpdateUserDto updateUserDto = new UpdateUserDto();
                    updateUserDto.setId(u.getId());
                    updateUserDto.setUsername(u.getUsername()+"_new");
                    return updateUserDto;
                }).toList();
        // wait for 1s
        TimeUnit.SECONDS.sleep(1);
        List<UserResponse> updatedUsers = userService.updateUsers(updateUserDtos);
        // assert that users are updated
        Optional<UserResponse> userResponse1Opt = updatedUsers.stream().filter(u -> "user1@gmail.com".equals(u.getEmail())).findFirst();
        assertTrue(userResponse1Opt.isPresent());
        UserResponse userResponse1 = userResponse1Opt.get();
        assertEquals(validUser1.getUsername()+"_new", userResponse1.getUsername());

        Optional<UserResponse> userResponse2Opt = updatedUsers.stream().filter(u -> "user2@gmail.com".equals(u.getEmail())).findFirst();
        assertTrue(userResponse2Opt.isPresent());
        UserResponse userResponse2 = userResponse2Opt.get();
        assertEquals(validUser2.getUsername()+"_new", userResponse2.getUsername());

        // assert lastUpdatedAt is updated, and has at least 1s difference with createdAt
        assertTrue(userResponse1.getCreatedAt().until(userResponse1.getLastUpdatedAt(), ChronoUnit.SECONDS) >= 1);
        assertTrue(userResponse2.getCreatedAt().until(userResponse2.getLastUpdatedAt(), ChronoUnit.SECONDS) >= 1);
    }

    @Test
    public void whenUpdateUsers_andAtLeastOneIDDoesNotExist_thenThrowUserNotFoundException() {
        UserResponse createdUser = userService.createUser(validUser1);

        UpdateUserDto updateUserDto1 = new UpdateUserDto();
        updateUserDto1.setId(createdUser.getId());
        updateUserDto1.setUsername(validUser1.getUsername()+"_new");

        // Update non-existing ID
        UpdateUserDto updateUserDto2 = new UpdateUserDto();
        updateUserDto2.setId(100L);
        updateUserDto2.setUsername("new_user");

        assertThrows(UserNotFoundException.class, () -> userService.updateUsers(List.of(updateUserDto1, updateUserDto2)));
    }

    @Test
    public void whenUpdateUsers_andUsernameOrEmailProvidedAlreadyTaken_thenThrowDuplicateUsernameOrEmailException() {
        List<UserResponse> createdUsers = createUsers();
        // update user but use already taken username/email
        Optional<UserResponse> userResponse1Opt = createdUsers.stream().filter(u -> "user1".equals(u.getUsername())).findFirst();
        assertTrue(userResponse1Opt.isPresent());
        UserResponse userResponse1 = userResponse1Opt.get();
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userResponse1.getId());
        updateUserDto.setUsername(validUser2.getUsername());

        assertThrows(DuplicateUsernameOrEmailException.class, () -> userService.updateUsers(List.of(updateUserDto)));

    }

    @Test
    public void whenDeleteUsers_thenReturnOk() {
        List<UserResponse> createdUsers = createUsers();
        // assert that repository has 2 user records
        assertEquals(2, userRepository.count());

        // delete created users
        List<Long> idsToDelete = createdUsers.stream().map(UserResponse::getId).toList();
        userService.softDeleteUsers(idsToDelete);

        // assert that both records are still in the db but deleted flags are set to true
        assertEquals(2, userRepository.count());
        userRepository.findAll().forEach(u -> assertTrue(u.isDeleted()));
    }

    @Test
    public void whenDeleteNonExistingUsers_thenStillReturnOk() {
        assertDoesNotThrow(() -> userService.softDeleteUsers(List.of(1L, 2L, 3L)));
    }

    private static void assertNewUsersAgainstUserDtoRequest(UserDto request, UserResponse response) {
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getUsername(), response.getUsername());
        assertEquals(request.getPassword(), response.getPassword());
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
        assertFalse(response.isDeleted());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getLastUpdatedAt());
        // assert createdAt and lastUpdatedAt are the same (up to seconds) upon record creation
        assertEquals(0, response.getCreatedAt().until(response.getLastUpdatedAt(), ChronoUnit.SECONDS));

    }

    private List<UserResponse> createUsers() {
        return List.of(
                userService.createUser(validUser1),
                userService.createUser(validUser2)
        );

    }

}
