package com.pccw.registrationservice.user.unit;

import com.pccw.registrationservice.user.UserController;
import com.pccw.registrationservice.user.UserService;
import com.pccw.registrationservice.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.pccw.registrationservice.user.exception.ErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class DeleteUserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    @Test
    public void givenIDs_whenDeleteUsers_thenReturnOk() throws Exception {
        List<Long> request = Arrays.asList(1L, 2L);
        willDoNothing().given(userService).softDeleteUsers(request);

        mvc.perform(delete("/users")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[]", // empty list
            "[\"random\"]" // non-numeric ID
    })
    public void givenInvalidRequestBody_whenDeleteUsers_thenReturn400(String content) throws Exception {
        mvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(content)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(MISSING_OR_INVALID_REQUEST_BODY_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Missing/invalid request body")));

    }

    @Test
    public void givenIDs_whenDeleteUsers_andUnknownErrorThrown_thenReturn500() throws Exception {
        List<Long> request = Arrays.asList(1L, 2L);
        willThrow(new IllegalArgumentException()).given(userService).softDeleteUsers(request);

        mvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code", is(INTERNAL_SERVER_ERROR_CODE)))
                .andExpect(jsonPath("message", is("Internal server error")));
    }


}
