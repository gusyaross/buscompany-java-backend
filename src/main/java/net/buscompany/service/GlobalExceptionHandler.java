package net.buscompany.service;

import net.buscompany.dto.response.error.ErrorDto;
import net.buscompany.dto.response.error.ErrorDtoResponse;
import net.buscompany.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleValidation(MethodArgumentNotValidException ex){
        List<ErrorDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(
                error -> errors.add(new ErrorDto(error.getCode(), error.getObjectName(), error.getDefaultMessage())));

        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoResponse handlerBadRequests(ServerException ex){
        return new ErrorDtoResponse(Collections.singletonList(new ErrorDto(ex.getErrorCode().toString(), ex.getField(), ex.getMessage())));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDtoResponse handlerNotFoundRequest(NoHandlerFoundException ex) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(new ErrorDto(ex.getClass().getName(), ex.getRequestURL(), ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

}
