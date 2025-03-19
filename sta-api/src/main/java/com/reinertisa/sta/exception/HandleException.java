package com.reinertisa.sta.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.reinertisa.sta.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.reinertisa.sta.utils.RequestUtils.handleErrorResponse;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error("handleExceptionInternal: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, statusCode), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error("handleMethodArgumentNotValid: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String fieldMessages = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(handleErrorResponse(fieldMessages, getRootCauseMessage(ex), request, statusCode), statusCode);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Response> apiException(ApiException ex) {
        log.error("ApiException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> badCredentialsException(BadCredentialsException ex) {
        log.error("BadCredentialsException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Response> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        log.error("SQLIntegrityConstraintViolationException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Response> unrecognizedPropertyException(UnrecognizedPropertyException ex) {
        log.error("UnrecognizedPropertyException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage().contains("Duplicate entry") ? "Information already exists" : ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> accessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse("Access denied. You don't have access.", getRootCauseMessage(ex), request, HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> exception(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(processErrorMessage(ex), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Response> transactionSystemException(TransactionSystemException ex) {
        log.error("TransactionSystemException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(processErrorMessage(ex), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Response> emptyResultDataAccessException(EmptyResultDataAccessException ex) {
        log.error("EmptyResultDataAccessException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Response> credentialsExpiredException(CredentialsExpiredException ex) {
        log.error("CredentialsExpiredException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Response> disabledException(DisabledException ex) {
        log.error("DisabledException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse("User account is currently disabled.", getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Response> lockedException(LockedException ex) {
        log.error("LockedException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response> duplicateKeyException(DuplicateKeyException ex) {
        log.error("DuplicateKeyException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(processErrorMessage(ex), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(processErrorMessage(ex), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response> dataAccessException(DataAccessException ex) {
        log.error("DataAccessException: {}", ex.getMessage());
        return new ResponseEntity<>(handleErrorResponse(processErrorMessage(ex), getRootCauseMessage(ex), request, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String processErrorMessage(Exception ex) {
        if (ex instanceof ApiException) {
            return ex.getMessage();
        }

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("duplicat") && ex.getMessage().contains("AccountVerifications")) {
                return "You already verified your account.";
            }
            if (ex.getMessage().contains("duplicate") && ex.getMessage().contains("ResetPasswordVerifications")) {
                return "We already sent you an email to reset your password.";
            }
            if (ex.getMessage().contains("duplicate") && ex.getMessage().contains("Key (email)")) {
                return "Email already exists. Use a different email and try again.";
            }
            if (ex.getMessage().contains("duplicate")) {
                return "Duplicate entry. Please try again.";
            }
        }
        return "An error occurred. Please try again.";
    }
}
