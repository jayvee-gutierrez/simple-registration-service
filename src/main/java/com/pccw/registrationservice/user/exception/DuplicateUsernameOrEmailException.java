package com.pccw.registrationservice.user.exception;

import com.pccw.registrationservice.exception.PCCWRuntimeException;

public class DuplicateUsernameOrEmailException  extends PCCWRuntimeException {

    public DuplicateUsernameOrEmailException(Throwable cause) {
        super(new ErrorMessage(ErrorCodes.DUPLICATE_USERNAME_OR_EMAIL_ERROR_CODE, "Username or email already taken"));
    }

    public DuplicateUsernameOrEmailException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage);
    }
}
