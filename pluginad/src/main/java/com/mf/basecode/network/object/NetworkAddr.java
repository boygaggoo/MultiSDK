package com.mf.basecode.network.object;


public class NetworkAddr {

  private String serverAddress;

  public NetworkAddr() {
  }

  public NetworkAddr(String serverAddress) {
    setServerAddress(serverAddress);
  }

  public String getServerAddress() {
    return serverAddress;
  }
  public void setServerAddress(String serverAddress) {
    if (serverAddress != null && serverAddress.toLowerCase().startsWith("http://")) {
      this.serverAddress = serverAddress;
    } else {
      this.serverAddress = "http://" + serverAddress;
    }
  }

  @Override
  public String toString() {
    return "NetworkAddr [serverAddress=" + serverAddress + "]";
  }
}
