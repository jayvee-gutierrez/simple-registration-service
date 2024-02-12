package com.pccw.registrationservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import static com.pccw.registrationservice.user.util.ValidationConstants.*;

@Data
public class UserDto {

    @NotBlank
    @Email
    @Schema(example="user1@email.com")
    private String email;

    @NotBlank
    @Pattern(regexp = USERNAME_PATTERN)
    private String username;

    @NotBlank
    @Pattern(regexp = PASSWORD_PATTERN)
    private String password;

    @NotBlank
    @Pattern(regexp = NAME_PATTERN)
    private String firstName;

    @NotBlank
    @Pattern(regexp = NAME_PATTERN)
    private String lastName;

}
