package kr.ac.pcu.cyber.userservice.controller;

import kr.ac.pcu.cyber.userservice.domain.dto.ErrorResponseData;
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
}
