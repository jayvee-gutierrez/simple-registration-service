package com.pccw.registrationservice.user.unit;

import com.pccw.registrationservice.user.UserController;
import com.pccw.registrationservice.user.UserService;
import com.pccw.registrationservice.user.dto.UpdateUserDto;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import com.pccw.registrationservice.user.exception.DuplicateUsernameOrEmailException;
import com.pccw.registrationservice.user.exception.ErrorMessage;
import com.pccw.registrationservice.user.exception.UserNotFoundException;
import com.pccw.registrationservice.util.JsonUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.pccw.registrationservice.user.exception.ErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UpdateUserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();


    private static UpdateUserDto updateUserDto1;

    private static UpdateUserDto updateUserDto2;

    private static UserResponse validUserResponse1;

    private static UserResponse validUserResponse2;

    @BeforeAll
    public static void init() {
        UserDto validUser1 = new UserDto();
        validUser1.setUsername("user1");
        validUser1.setEmail("user1_new@gmail.com");
        validUser1.setPassword("P@ssw0rd");
        validUser1.setFirstName("User");
        validUser1.setLastName("One");

        UserDto validUser2 = new UserDto();
        validUser2.setUsername("user2new");
        validUser2.setEmail("user2@gmail.com");
        validUser2.setPassword("P@ssw0rd");
        validUser2.setFirstName("User");
        validUser2.setLastName("Two");

        updateUserDto1 = new UpdateUserDto();
        updateUserDto1.setId(1L);
        updateUserDto1.setEmail("user1_new@gmail.com");

        updateUserDto2 = new UpdateUserDto();
        updateUserDto2.setId(2L);
        updateUserDto2.setUsername("user2new");


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
    public void givenUsers_whenUpdateUsers_thenReturnUpdatedUsers() throws Exception {
        List<UpdateUserDto> request = Arrays.asList(updateUserDto1, updateUserDto2);
        given(userService.updateUsers(request)).willReturn(Arrays.asList(validUserResponse1, validUserResponse2));

        mvc.perform(patch("/users")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.id == 2)].username", contains("user2new")))
                .andExpect(jsonPath("$[?(@.id == 1)].email", contains("user1_new@gmail.com")));

    }

    @Test
    public void givenUsers_whenUpdateUsers_andAtLeastOneIsNotFound_thenReturn404() throws Exception {
        List<UpdateUserDto> request = Arrays.asList(updateUserDto1, updateUserDto2);
        given(userService.updateUsers(request)).willThrow(new UserNotFoundException(
                new ErrorMessage(
                    USER_NOT_FOUND_ERROR_CODE, "At least one of the provided user IDs does not exist")));

        mvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(USER_NOT_FOUND_ERROR_CODE)))
                .andExpect(jsonPath("message", is("At least one of the provided user IDs does not exist")));

    }

    @Test
    public void givenUsers_whenUpdateUsers_andDuplicateEmailOrUsernameIsProvided_thenReturn400() throws Exception {
        List<UpdateUserDto> request = Arrays.asList(updateUserDto1, updateUserDto2);
        given(userService.updateUsers(request)).willThrow(new DuplicateUsernameOrEmailException(
                new ErrorMessage(
                        DUPLICATE_USERNAME_OR_EMAIL_ERROR_CODE, "At least one of the usernames/emails provided already taken"),
                new DataIntegrityViolationException("", new Exception())
                ));

        mvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(DUPLICATE_USERNAME_OR_EMAIL_ERROR_CODE)))
                .andExpect(jsonPath("message", is("At least one of the usernames/emails provided already taken")));

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[]", // empty list
            "[ {\"email\":\"something@gmail.com\"} ]", // one of the IDs provided is null
            "[ {\"id\":1, \"username\":\"@@@***\"} ]" // one of the fields has invalid format
    })
    public void givenUsers_whenUpdateUsers_andInvalidRequest_thenReturn400(String content) throws Exception {
        mvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(VALIDATION_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Invalid request")));

    }

    @Test
    public void givenUsers_whenUpdateUsers_andMissingRequestBody_thenReturn400() throws Exception {
        mvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(MISSING_OR_INVALID_REQUEST_BODY_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Missing/invalid request body")));

    }

    @Test
    public void givenUsers_whenUpdateUsers_andInvalidMethod_thenReturn405() throws Exception {
        List<UpdateUserDto> request = Arrays.asList(updateUserDto1, updateUserDto2);
        given(userService.updateUsers(request)).willThrow(new IllegalArgumentException());

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(METHOD_NOT_ALLOWED_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Invalid request")));

    }

    @Test
    public void givenUsers_whenUpdateUsers_andUnknownError_thenReturn500() throws Exception {
        List<UpdateUserDto> request = Arrays.asList(updateUserDto1, updateUserDto2);
        given(userService.updateUsers(request)).willThrow(new IllegalArgumentException());

        mvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(INTERNAL_SERVER_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Internal server error")));

    }


}
