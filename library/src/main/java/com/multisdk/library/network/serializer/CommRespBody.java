package com.multisdk.library.network.serializer;

public class CommRespBody {

  @ByteField(index = 0, description = "错误代码") private int errorCode;

  @ByteField(index = 1, description = "提示消息内容") private String errorMessage;

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
