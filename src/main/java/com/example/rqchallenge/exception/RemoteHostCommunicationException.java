package com.example.rqchallenge.exception;

public class RemoteHostCommunicationException extends RuntimeException{

  public RemoteHostCommunicationException(String message, Throwable cause){
    super(message, cause);
  }
  public RemoteHostCommunicationException(String message){
    super(message);
  }

}
