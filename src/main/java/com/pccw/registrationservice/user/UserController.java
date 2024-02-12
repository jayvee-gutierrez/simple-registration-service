package com.pccw.registrationservice.user;

import com.pccw.registrationservice.user.dto.UpdateUserDto;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Users API")
@RequestMapping("users")

public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper = new ModelMapper();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register new user", description = "Returns the created user")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserDto user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(summary = "Get user by ID", description = "Returns the user with the provided ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        UserResponse userResponse = modelMapper.map(userService.getUser(id), UserResponse.class);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Get all registered user", description = "Returns all the registered users or an empty list if there are no registered users")
    @GetMapping
    // TODO: Implement pagination
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Update user(s)", description = "Returns the list of users with the updated data")
    @PatchMapping
    public ResponseEntity<List<UserResponse>> updateUsers(@RequestBody @Valid @NotEmpty List<UpdateUserDto> updateUsersRequest) {
        return ResponseEntity.ok(userService.updateUsers(updateUsersRequest));
    }

    @Operation(summary = "Soft delete user(s)", description = "Returns a successful response even if one or more of the provided IDs do not exist")
    @DeleteMapping
    public ResponseEntity<Void> deleteUsers(@RequestBody @NotEmpty List<Long> userIds) {
        userService.softDeleteUsers(userIds);
        return ResponseEntity.ok().build();
    }


}
