package com.pccw.registrationservice.exception;

import com.pccw.registrationservice.user.exception.ErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PCCWRuntimeException extends RuntimeException{

    private ErrorMessage errorMessage;

    public  PCCWRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public  PCCWRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }

}
