package kr.ac.pcu.cyber.userservice.controller;

import io.jsonwebtoken.MalformedJwtException;
import kr.ac.pcu.cyber.userservice.domain.dto.ErrorResponseData;
import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import kr.ac.pcu.cyber.userservice.errors.InvalidTokenException;
import kr.ac.pcu.cyber.userservice.errors.TokenExpiredException;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponseData handleNotFoundUser(UserNotFoundException e) {
        return new ErrorResponseData(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(EmptyCookieException.class)
    public ErrorResponseData handle(EmptyCookieException e) {
        return new ErrorResponseData(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorResponseData handle(InvalidTokenException e) {
        return new ErrorResponseData(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ErrorResponseData handle(TokenExpiredException e) {
        return new ErrorResponseData(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MalformedJwtException.class)
    public ErrorResponseData handle(MalformedJwtException e) {
        return new ErrorResponseData(e.getMessage() + " 변경요망");
    }
}
