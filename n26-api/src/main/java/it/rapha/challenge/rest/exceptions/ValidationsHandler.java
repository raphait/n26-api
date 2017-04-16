package it.rapha.challenge.rest.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequestMapping(produces = "application/json")
public class ValidationsHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = BAD_REQUEST)
	public Map handleConstraintViolationException(MethodArgumentNotValidException ex) {
		return ex.getBindingResult().getAllErrors().stream()
				//.map(e -> e.getDefaultMessage())
				.collect(Collectors.toMap(
						ObjectError::getObjectName, ObjectError::getDefaultMessage, 
						(c, m) -> c + m));
	}

}
