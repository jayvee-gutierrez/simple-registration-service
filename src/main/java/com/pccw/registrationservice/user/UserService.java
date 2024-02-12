package com.pccw.registrationservice.user;

import com.pccw.registrationservice.notification.SimpleMailService;
import com.pccw.registrationservice.notification.dto.SimpleMail;
import com.pccw.registrationservice.user.dto.UpdateUserDto;
import com.pccw.registrationservice.user.dto.UserDto;
import com.pccw.registrationservice.user.dto.UserResponse;
import com.pccw.registrationservice.user.exception.DuplicateUsernameOrEmailException;
import com.pccw.registrationservice.user.exception.ErrorCodes;
import com.pccw.registrationservice.user.exception.ErrorMessage;
import com.pccw.registrationservice.user.exception.UserNotFoundException;
import com.pccw.registrationservice.user.model.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Value("${pccw.email.enabled}")
    private boolean emailEnabled;

    @Value("${pccw.email.default-subject}")
    private String subject;

    private final UserRepository userRepository;

    private final SimpleMailService mailService;

    private final ModelMapper modelMapper = new ModelMapper();

    public UserService(UserRepository userRepository, SimpleMailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    public UserResponse createUser(UserDto userRequest) {
        try {
            User user = modelMapper.map(userRequest, User.class);
            userRepository.save(user);
            // TODO: Add email confirmation step, implement event-driven email sending
            if(emailEnabled) {
                log.info("Sending registration confirmation email");
                String body = String.format("Congratulations! You are successfully registered to PCCW. Your username is: %s", user.getUsername());
                SimpleMail message = new SimpleMail(user.getEmail(), subject, body);
                mailService.sendSimpleEmail(message);
            }
            return modelMapper.map(user, UserResponse.class);
        } catch (DataIntegrityViolationException dive) {
            throw new DuplicateUsernameOrEmailException(dive);
        }
    }

    public UserResponse getUser(long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponse.class);
    }

    public List<UserResponse> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(u -> modelMapper.map(u, UserResponse.class)).toList();
    }

    @Transactional
    public List<UserResponse> updateUsers(List<UpdateUserDto> updateUsersRequest) {
        try {
            List<User> updatedUsers = updateUsersRequest.stream().map(u -> {
                User updatedUser = modelMapper.map(u, User.class);
                User currUser = userRepository.findById(updatedUser.getId())
                        .orElseThrow(() -> new UserNotFoundException(
                                new ErrorMessage(ErrorCodes.USER_NOT_FOUND_ERROR_CODE, "At least one of the provided user IDs does not exist")));
                copyNonNullUserProperties(updatedUser, currUser);
                return currUser;
            }).toList();
            userRepository.saveAllAndFlush(updatedUsers);
            return updatedUsers.stream().map(u -> modelMapper.map(u, UserResponse.class)).toList();
        } catch (DataIntegrityViolationException dive) {
            throw new DuplicateUsernameOrEmailException(
                    new ErrorMessage(ErrorCodes.DUPLICATE_USERNAME_OR_EMAIL_ERROR_CODE, "At least one of the usernames/emails provided already taken"),
                    dive);
        }
    }

    @Transactional
    public void softDeleteUsers(List<Long> userIds) {
        userRepository.deleteAllById(userIds);
    }

    /**
     * Copy non-null, updatable properties from source {@link User} object to target {@link User} object
     * @param source The source {@link User} object
     * @param target The target {@link User} object
     */
    private static void copyNonNullUserProperties(User source, User target) {
        Optional.ofNullable(source.getFirstName()).ifPresent(target::setFirstName);
        Optional.ofNullable(source.getLastName()).ifPresent(target::setLastName);
        Optional.ofNullable(source.getUsername()).ifPresent(target::setUsername);
        Optional.ofNullable(source.getPassword()).ifPresent(target::setPassword);
        Optional.ofNullable(source.getEmail()).ifPresent(target::setEmail);
    }


}
