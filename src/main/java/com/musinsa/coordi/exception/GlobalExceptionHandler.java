package com.musinsa.coordi.exception;

import com.musinsa.coordi.controller.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({BindException.class, MissingRequestValueException.class, IllegalArgumentException.class,
            NotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleBadRequest(Exception e) {
        log.warn("bad request: ", e);
        return new Response(false, HttpStatus.BAD_REQUEST.name(), e.getMessage());
    }

    @ExceptionHandler({AlreadyExistException.class, InvalidDataException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response handleConflict(Exception e) {
        log.warn("conflict: ", e);
        return new Response(false, HttpStatus.BAD_REQUEST.name(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleException(Exception e) {
        log.error("unknown error: ", e);
        return new Response(false, HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage());
    }

}
