package com.pccw.registrationservice.user.exception;

import com.pccw.registrationservice.exception.PCCWRuntimeException;

public class UserNotFoundException extends PCCWRuntimeException {
    public UserNotFoundException() {
        super(new ErrorMessage(ErrorCodes.USER_NOT_FOUND_ERROR_CODE, "User not found"));
    }

    public UserNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

}
