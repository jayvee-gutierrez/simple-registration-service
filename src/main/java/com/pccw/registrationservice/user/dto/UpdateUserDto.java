package com.pccw.registrationservice.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.pccw.registrationservice.user.util.ValidationConstants.*;
import static com.pccw.registrationservice.user.util.ValidationConstants.NAME_PATTERN;

@Data
public class UpdateUserDto {

    @NotNull
    private Long id;

    @Email
    private String email;

    @Pattern(regexp = USERNAME_PATTERN)
    private String username;

    @Pattern(regexp = PASSWORD_PATTERN)
    private String password;

    @Pattern(regexp = NAME_PATTERN)
    private String firstName;

    @Pattern(regexp = NAME_PATTERN)
    private String lastName;

}
