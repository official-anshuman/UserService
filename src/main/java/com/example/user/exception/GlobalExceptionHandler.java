package com.example.user.exception;

import com.example.user.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler  {
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Response>> webExchangeBindExceptionHandler(Exception exception){
        String errorMessage="";
        if(exception instanceof WebExchangeBindException){
            errorMessage = ((WebExchangeBindException) exception).getAllErrors().get(0).getDefaultMessage();
        }
        Response response = new Response(400,errorMessage,null);
        return Mono.just(ResponseEntity.ok(response));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<Response>> userNotFoundExceptionHandler(RuntimeException exception){
        Response response = new Response(400,exception.getMessage(),null);
        return Mono.just(ResponseEntity.ok(response));
    }
}
