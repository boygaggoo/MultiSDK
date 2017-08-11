package com.multisdk.library.network.exception;

public class HttpException extends Exception {

  public static final int HTTP_ERROR = 1;
  public static final int HTTP_ERROR_GET_MSG = 2;

  public int errorCode;

  public String errorMsg;

  public Throwable throwable;

  public HttpException(int errorCode){
    this.errorCode = errorCode;
    this.errorMsg = "";
  }

  public HttpException(int errorCode,String errorMsg){
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public HttpException(int errorCode,Throwable throwable){
    this.errorCode = errorCode;
    this.throwable = throwable;
  }

  public HttpException(int errorCode,String errorMsg,Throwable throwable){
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
    this.throwable = throwable;
  }

  @Override public String toString() {
    return "HttpException{" + "errorCode=" + errorCode + ", errorMsg='" + errorMsg + '\'' + '}';
  }

}
