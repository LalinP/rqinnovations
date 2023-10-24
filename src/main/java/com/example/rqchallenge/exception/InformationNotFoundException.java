package com.example.rqchallenge.exception;

import java.util.function.Supplier;

public class InformationNotFoundException extends RuntimeException {

  public InformationNotFoundException(String message, Throwable cause){
    super(message, cause);
  }
  public InformationNotFoundException(String message){
    super(message);
  }

}
