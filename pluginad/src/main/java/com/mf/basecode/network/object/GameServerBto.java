package com.mf.basecode.network.object;

import com.mf.basecode.network.serializer.ByteField;

public class GameServerBto {

  @ByteField(index = 1, description = "模块标识符")
  private int  moduleId;

  @ByteField(index = 2)
  private String host;

  @ByteField(index = 3)
  private int    port;

  /**
   * @return the moduleId
   */
  public int getModuleId() {
    return moduleId;
  }

  /**
   * @param moduleId
   *          the moduleId to set
   */
  public void setModuleId(int moduleId) {
    this.moduleId = moduleId;
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host
   *          the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port
   *          the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "GameServerBto [moduleId=" + moduleId + ", host=" + host + ", port=" + port + "]";
  }

}
