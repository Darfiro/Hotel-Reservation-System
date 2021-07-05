package com.rsoi.payments.controller;

import exceptions.*;
import model.BasicError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice
public class ErrorHandler
{
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicError badRequestHandler(BadRequestException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BasicError notFoundHandler(NotFoundException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BasicError conflictHandler(ConflictException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(NoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BasicError noContentHandler(NoContentException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(UprocessedEnityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BasicError unsupportedEntityHandler(UprocessedEnityException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BasicError internalServerErrorHandler(InternalServerErrorException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public BasicError serviceUnavailableHandler(ServiceUnavailableException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BasicError forbiddenHandler(ForbiddenException ex)
    {
        return new BasicError(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BasicError unauthorizedHandler(UnauthorizedException ex)
    {
        return new BasicError(ex.getMessage());
    }

}
