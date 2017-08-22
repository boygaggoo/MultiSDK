package com.xdd.pay.network.object;

import java.util.ArrayList;
import java.util.List;

public class NetworkAddr {
  /**
   * 服务地址，包括端口
   */
  private String serverAddress;
  private List<String> serverAddressList;
  private Integer serverAddressType;

  public NetworkAddr() {
  }

  public NetworkAddr(String serverAddress) {
    setServerAddress(serverAddress);
  }
  public NetworkAddr(List<String> serverAddressList, Integer serverAddressType) {
	  this.serverAddressType = serverAddressType;
	  setServerAddressList(serverAddressList);
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

  public List<String> getServerAddressList() {
	return serverAddressList;
  }
  public void setServerAddressList(List<String> addressList) {
	serverAddressList = new ArrayList<String>();
	for (String serverAddress : addressList) {
		if (null != serverAddress && serverAddress.toLowerCase().startsWith("http://")) {
			serverAddressList.add(serverAddress);
		} else {
			serverAddressList.add("http://" + serverAddress);
		}
	}
  }

  public Integer getServerAddressType() {
	return serverAddressType;
  }

@Override
  public String toString() {
    return "NetworkAddr [serverAddress=" + serverAddress + "]";
  }

}
