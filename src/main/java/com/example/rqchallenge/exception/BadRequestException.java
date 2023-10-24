package com.example.rqchallenge.exception;

public class BadRequestException extends RuntimeException {

  public BadRequestException(String message, Throwable cause){
    super(message, cause);
  }
  public BadRequestException(String message){
    super(message);
  }

}
