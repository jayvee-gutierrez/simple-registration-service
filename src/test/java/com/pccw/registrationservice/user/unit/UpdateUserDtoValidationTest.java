package com.pccw.registrationservice.user.unit;

import com.pccw.registrationservice.user.dto.UpdateUserDto;
import com.pccw.registrationservice.user.dto.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateUserDtoValidationTest {

    private static Validator validator;

    private static UpdateUserDto validUpdateUser;

    @BeforeAll
    public static void setup() {
        validUpdateUser = new UpdateUserDto();
        validUpdateUser.setId(1L);

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static UpdateUserDto clone(UpdateUserDto userToClone) {
        UpdateUserDto clone = new UpdateUserDto();
        BeanUtils.copyProperties(userToClone, clone);
        return clone;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "notemail.com",
            "...com"
    })
    public void testUpdateUserDtoValidation_invalidEmail(String email) {
        UpdateUserDto user = clone(validUpdateUser);
        user.setEmail(email);
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(user);
        violations.forEach(System.out::println);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("email", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be email");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user", // too short
            "user12345678899999999999", // too long
            "user@@@", // with special characters
            "" // blank
    })
    public void testUpdateUserDtoValidation_invalidUpdateUsernameFormat(String username) {
        UpdateUserDto user = clone(validUpdateUser);
        user.setUsername(username);
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("username", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be username");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pass", // too short
            "pass12345678899999999999", // too long
            "Password1", // no special character
            "password1@", // no uppercase
            "PASSWORD1@", // no lowercase
            "Password@@", // no number
            "" // blank
    })
    public void testUpdateUserDtoValidation_invalidPasswordFormat(String password) {
        UpdateUserDto user = clone(validUpdateUser);
        user.setPassword(password);
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("password", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "John@@@@", // has special character
            "Johnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn", // too long
            "" // blank
    })
    public void testUpdateUserDtoValidation_invalidFirstNameFormat(String firstName) {
        UpdateUserDto user = clone(validUpdateUser);
        user.setFirstName(firstName);
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("firstName", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Doe++++", // has special character
            "Doeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", // too long
            "" // blank
    })
    public void testUpdateUserDtoValidation_invalidLastNameFormat(String lastName) {
        UpdateUserDto user = clone(validUpdateUser);
        user.setLastName(lastName);
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("lastName", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

}
