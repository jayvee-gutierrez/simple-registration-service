package com.pccw.registrationservice.user.unit;

import com.pccw.registrationservice.user.UserController;
import com.pccw.registrationservice.user.UserService;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import com.pccw.registrationservice.user.exception.DuplicateUsernameOrEmailException;
import com.pccw.registrationservice.util.JsonUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.pccw.registrationservice.user.exception.ErrorCodes.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class RegisterUserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    private static UserDto validUser;

    private static UserResponse validUserResponse;

    @BeforeAll
    public static void init() {
        validUser = new UserDto();
        validUser.setUsername("user1");
        validUser.setEmail("user1@gmail.com");
        validUser.setPassword("P@ssw0rd");
        validUser.setFirstName("User");
        validUser.setLastName("One");

        validUserResponse = MODEL_MAPPER.map(validUser, UserResponse.class);
        validUserResponse.setId(1);
        validUserResponse.setCreatedAt(Instant.now());
        validUserResponse.setLastUpdatedAt(Instant.now());
    }

    @Test
    public void givenUser_whenRegisterUser_thenReturnUser() throws Exception {

        given(userService.createUser(validUser)).willReturn(validUserResponse);

        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(validUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("username", is("user1")));

    }

    @Test
    public void givenUser_whenRegisterUser_andInvalidBody_thenReturn400() throws Exception {

        UserDto user1 = new UserDto();
        user1.setUsername("user1@@@"); // invalid username
        user1.setEmail("user1@gmail.com");
        user1.setPassword("P@ssw0rd");
        user1.setFirstName("User");
        user1.setLastName("One");

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(user1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is("ValidationError")))
                .andExpect(jsonPath("message", is("Invalid request")));

    }

    @Test
    public void givenUser_whenRegisterUser_andUsernameOrEmailAlreadyTaken_thenReturn400() throws Exception {

        UserDto user1 = new UserDto();
        user1.setUsername("user1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("P@ssw0rd");
        user1.setFirstName("User");
        user1.setLastName("One");

        given(userService.createUser(user1)).willThrow(
                new DuplicateUsernameOrEmailException(new DataIntegrityViolationException("Unique constraint violated", new Exception())));

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(user1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(DUPLICATE_USERNAME_OR_EMAIL_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Username or email already taken")));

    }

    @Test
    public void givenUser_whenRegisterUser_andRequestBodyIsMissing_thenReturn400() throws Exception {
        UserDto user1 = new UserDto();
        user1.setUsername("user1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("P@ssw0rd");
        user1.setFirstName("User");
        user1.setLastName("One");

        given(userService.createUser(any())).willThrow(
                new DuplicateUsernameOrEmailException(new DataIntegrityViolationException("Unique constraint violated", new Exception())));

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(MISSING_OR_INVALID_REQUEST_BODY_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Missing/invalid request body")));

    }

    @Test
    public void givenUser_whenRegisterUser_andUnknownExceptionIsThrown_thenReturn500() throws Exception {

        given(userService.createUser(any())).willThrow(new IllegalArgumentException());

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(validUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(INTERNAL_SERVER_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Internal server error")));

    }

    @Test
    public void givenUser_whenRegisterUser_andInvalidMethod_thenReturn405() throws Exception {

        given(userService.createUser(any())).willThrow(new IllegalArgumentException());

        mvc.perform(patch("/users/register")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(validUser)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(METHOD_NOT_ALLOWED_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Invalid request")));

    }
}
