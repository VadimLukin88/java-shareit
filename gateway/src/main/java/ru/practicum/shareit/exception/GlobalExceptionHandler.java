package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.TreeMap;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(DataNotFoundException.class)
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> handleDataNotFoundException(DataNotFoundException e) {
//        return new ResponseEntity<>(Map.of("Error message", e.getMessage()), HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new TreeMap<>();
        errors.put("Error message", "Некорректное значение в теле запроса");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put("Details", error.getField() + " / " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(Map.of("Error message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return new ResponseEntity<>(Map.of("Error message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    @ResponseBody
//    ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
//         return new ResponseEntity<>(Map.of("Error message", e.getMessage()), HttpStatus.CONFLICT);
//    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return new ResponseEntity<>(Map.of("Error message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
