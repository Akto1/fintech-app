package com.example.fintech_app.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
public  ResponseEntity<Map<String,String>> buildException(String error, String message, HttpStatus status){
return ResponseEntity.status(status).body(Map.of(
        "error",error,
        "message",message,
        "status",String.valueOf(status)));
}

@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> NotCorrectExcept(Exception e){
    return buildException("Некорректный запрос : ",e.getMessage(),HttpStatus.BAD_REQUEST);
}


@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<Map<String,String>> handleUserNotFound(UserNotFoundException e){
    return buildException("Пользователь не найден  : ", e.getMessage(),HttpStatus.NOT_FOUND);
}

@ExceptionHandler(InsufficientFundsException.class)
public ResponseEntity<Map<String,String>> handleInsufficientFunds(InsufficientFundsException e){
    return buildException("Недостаточно средств : ",e.getMessage(),HttpStatus.UNPROCESSABLE_ENTITY);
}

@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<Map<String,String>> handleBadCredentials(BadCredentialsException e){
    return buildException("Ошибка аутентификации : ",e.getMessage(), HttpStatus.UNAUTHORIZED);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Ошибка валидации");

        return buildException("Ошибка валидации", errorMessage, HttpStatus.BAD_REQUEST);
    }

}
