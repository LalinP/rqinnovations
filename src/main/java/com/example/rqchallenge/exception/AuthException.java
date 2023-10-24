package com.example.rqchallenge.exception;

public class AuthException extends RuntimeException {

  public AuthException(String message, Throwable cause){
    super(message, cause);
  }
  public AuthException(String message){
    super(message);
  }

}
