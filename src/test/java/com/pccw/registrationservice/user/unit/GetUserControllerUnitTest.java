package com.pccw.registrationservice.user.unit;

import com.pccw.registrationservice.user.UserController;
import com.pccw.registrationservice.user.UserService;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import com.pccw.registrationservice.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.pccw.registrationservice.user.exception.ErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class GetUserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    private static UserResponse validUserResponse1;

    private static UserResponse validUserResponse2;

    @BeforeAll
    public static void init() {
        UserDto validUser1 = new UserDto();
        validUser1.setUsername("user1");
        validUser1.setEmail("user1@gmail.com");
        validUser1.setPassword("P@ssw0rd");
        validUser1.setFirstName("User");
        validUser1.setLastName("One");

        UserDto validUser2 = new UserDto();
        validUser2.setUsername("user2");
        validUser2.setEmail("user2@gmail.com");
        validUser2.setPassword("P@ssw0rd");
        validUser2.setFirstName("User");
        validUser2.setLastName("Two");

        validUserResponse1 = MODEL_MAPPER.map(validUser1, UserResponse.class);
        validUserResponse1.setId(1);
        validUserResponse1.setCreatedAt(Instant.now());
        validUserResponse1.setLastUpdatedAt(Instant.now());

        validUserResponse2 = MODEL_MAPPER.map(validUser2, UserResponse.class);
        validUserResponse2.setId(2);
        validUserResponse2.setCreatedAt(Instant.now());
        validUserResponse2.setLastUpdatedAt(Instant.now());
    }

    @Test
    public void givenID_whenGetUser_thenReturnUser() throws Exception {

        given(userService.getUser(1)).willReturn(validUserResponse1);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("username", is("user1")))
                .andExpect(jsonPath("email", is("user1@gmail.com")))
                .andExpect(jsonPath("firstName", is("User")))
                .andExpect(jsonPath("lastName", is("One")))
                .andExpect(jsonPath("password").doesNotExist());

    }

    @Test
    public void givenID_whenGetUser_andUserNotFound_thenReturn404() throws Exception {

        given(userService.getUser(1)).willThrow(new UserNotFoundException());

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(USER_NOT_FOUND_ERROR_CODE)))
                .andExpect(jsonPath("message", is("User not found")));

    }

    @Test
    public void givenNonNumericID_whenGetUser_thenReturn400() throws Exception {

        given(userService.getUser(1)).willThrow(new UserNotFoundException());

        mvc.perform(get("/users/{id}", "random"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(VALIDATION_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Invalid request")));

    }

    @Test
    public void givenID_whenGetUser_andUnknownErrorThrown_thenReturn500() throws Exception {

        given(userService.getUser(1)).willThrow(new IllegalArgumentException());

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(INTERNAL_SERVER_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Internal server error")));

    }

    @Test
    public void whenGetAllUsers_thenReturnAllUsers() throws Exception {

        given(userService.getAllUsers()).willReturn(Arrays.asList(validUserResponse1, validUserResponse2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1,2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("user1", "user2")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("user1@gmail.com", "user2@gmail.com")));

    }

    @Test
    public void whenGetAllUsers_andNoUsers_thenReturnEmptyList() throws Exception {

        given(userService.getAllUsers()).willReturn(List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    public void whenGetAllUsers_andUnknownErrorThrown_thenReturn500() throws Exception {

        given(userService.getAllUsers()).willThrow(new IllegalArgumentException());

        mvc.perform(get("/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(INTERNAL_SERVER_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Internal server error")));

    }

}
