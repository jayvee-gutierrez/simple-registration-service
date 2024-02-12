package com.pccw.registrationservice.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.pccw.registrationservice.user.exception.ErrorCodes.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {


    @ExceptionHandler({ UserNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ex.getErrorMessage();
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, HandlerMethodValidationException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleInvalidRequest(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        // TODO: Parse exception to at least return the field violating the validations
        return new ErrorMessage(VALIDATION_ERROR_CODE, "Invalid request");
    }

    @ExceptionHandler({ DuplicateUsernameOrEmailException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDuplicateUsernameOrEmailException(DuplicateUsernameOrEmailException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ex.getErrorMessage();
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorMessage catchMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return new ErrorMessage(METHOD_NOT_ALLOWED_ERROR_CODE, "Invalid request");
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage catchHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return new ErrorMessage(MISSING_OR_INVALID_REQUEST_BODY_ERROR_CODE, "Missing/invalid request body");
    }

    @ExceptionHandler({ NoResourceFoundException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorMessage catchNoResourceFound(NoResourceFoundException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return new ErrorMessage(INVALID_RESOURCE_ERROR_CODE, "Invalid request");
    }

    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage catchAll(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return new ErrorMessage(INTERNAL_SERVER_ERROR_CODE, "Internal server error");
    }

}
