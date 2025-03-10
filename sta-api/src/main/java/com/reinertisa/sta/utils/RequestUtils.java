package com.reinertisa.sta.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reinertisa.sta.domain.Response;
import com.reinertisa.sta.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class RequestUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse =
            (httpServletResponse, response) -> {
                try {
                    var outputStream = httpServletResponse.getOutputStream();
                    new ObjectMapper().writeValue(outputStream, response);
                    outputStream.flush();
                } catch (Exception ex) {
                    throw new ApiException(ex.getMessage());
                }
            };

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(HttpStatus.FORBIDDEN)) {
            return "You do not have enough permission.";
        }
        if (httpStatus.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            return "You are not logged in.";
        }
        if (exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof CredentialsExpiredException ||
                exception instanceof ApiException) {
            return exception.getMessage();
        }
        if (httpStatus.is5xxServerError()) {
            return "An internal error occurred.";
        } else {
            return "An error occurred. Please try again.";
        }
    };

    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data
        );
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                           Exception exception) {
        if (exception instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, exception, HttpStatus.FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }

    }

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                             Exception exception, HttpStatus status) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception, status),
                getRootCauseMessage(exception),
                Collections.emptyMap());
    }
}
