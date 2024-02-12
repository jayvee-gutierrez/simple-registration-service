package com.pccw.registrationservice.user.unit;

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

public class UserDtoValidationTest {

    private static Validator validator;

    private static UserDto validUser;

    @BeforeAll
    public static void setup() {
        validUser = new UserDto();
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setPassword("P@ssw0rd");
        validUser.setEmail("johndoe@email.com");
        validUser.setUsername("john_doe");

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static UserDto clone(UserDto userToClone) {
        UserDto clone = new UserDto();
        BeanUtils.copyProperties(userToClone, clone);
        return clone;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "notemail.com",
            "" // blank
    })
    public void testUserDtoValidation_invalidEmail(String email) {
        UserDto user = clone(validUser);
        user.setEmail(email);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        violations.forEach(System.out::println);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("email", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be email");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user", // too short
            "user12345678899999999999", // too long
            "user@@@" // with special characters
    })
    public void testUserDtoValidation_invalidUsernameFormat(String username) {
        UserDto user = clone(validUser);
        user.setUsername(username);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("username", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be username");
    }

    @Test
    public void testUserDtoValidation_blankUsername() {
        UserDto user = clone(validUser);
        user.setUsername("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Must have 2 violations");
        violations.forEach(v -> assertEquals("username", v.getPropertyPath().toString(), "Violating field must be username"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pass", // too short
            "pass12345678899999999999", // too long
            "Password1", // no special character
            "password1@", // no uppercase
            "PASSWORD1@", // no lowercase
            "Password@@", // no number
    })
    public void testUserDtoValidation_invalidPasswordFormat(String password) {
        UserDto user = clone(validUser);
        user.setPassword(password);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("password", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

    @Test
    public void testUserDtoValidation_blankPassword() {
        UserDto user = clone(validUser);
        user.setPassword("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Must have 2 violations");
        violations.forEach(v -> assertEquals("password", v.getPropertyPath().toString(), "Violating field must be password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "John@@@@", // has special character
            "Johnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" // too long
    })
    public void testUserDtoValidation_invalidFirstNameFormat(String firstName) {
        UserDto user = clone(validUser);
        user.setFirstName(firstName);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("firstName", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

    @Test
    public void testUserDtoValidation_blankFirstName() {
        UserDto user = clone(validUser);
        user.setFirstName("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Must have 2 violations");
        violations.forEach(v -> assertEquals("firstName", v.getPropertyPath().toString(), "Violating field must be password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Doe++++", // has special character
            "Doeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" // too long
    })
    public void testUserDtoValidation_invalidLastNameFormat(String lastName) {
        UserDto user = clone(validUser);
        user.setLastName(lastName);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Must have 1 violation");
        assertEquals("lastName", violations.stream().toList().get(0).getPropertyPath().toString(), "Violating field must be password");
    }

    @Test
    public void testUserDtoValidation_blankLastName() {
        UserDto user = clone(validUser);
        user.setLastName("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Must have 2 violations");
        violations.forEach(v -> assertEquals("lastName", v.getPropertyPath().toString(), "Violating field must be password"));
    }
}
