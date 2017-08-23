package com.mf.basecode.network.serializer;

import java.io.Serializable;

public class MFCom_ResponseBody implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  @ByteField(index = 0, description = "错误代码")
  private int               errorCode;

  @ByteField(index = 1, description = "提示消息内容")
  private String            errorMessage;

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

  @Override
  public String toString() {
    return "MFCom_ResponseBody [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
  }
}
