package com.mf.basecode.utils.contants;

import java.io.Serializable;
import java.util.UUID;

import com.mf.basecode.network.object.NetworkAddr;

public final class Session implements Serializable {
  private static final long serialVersionUID  = 1044237039063420007L;
  private static Session    session           = null;
  // 交叉推广网络地址
  private NetworkAddr       promNetworkAddr   = null;
  // 数据统计网络地址
  private NetworkAddr       statisNetworkAddr = null;
  // 更新服务器网络地址
  private NetworkAddr       updateNetworkAddr = null;

  private String            UUId;

  private Session() {
    UUId = UUID.randomUUID().toString();
  }
  public NetworkAddr getPromNetworkAddr() {
    return getInstance().promNetworkAddr;
  }
  public void setPromNetworkAddr(NetworkAddr promNetworkAddr) {
    getInstance().promNetworkAddr = promNetworkAddr;
  }
  public NetworkAddr getStatisNetworkAddr() {
    return statisNetworkAddr;
  }
  public void setStatisNetworkAddr(NetworkAddr statisNetworkAddr) {
    getInstance().statisNetworkAddr = statisNetworkAddr;
  }
  public NetworkAddr getUpdateNetworkAddr() {
    return updateNetworkAddr;
  }
  public void setUpdateNetworkAddr(NetworkAddr updateNetworkAddr) {
    getInstance().updateNetworkAddr = updateNetworkAddr;
  }
  @Override
  public String toString() {
    return "Session [promNetworkAddr=" + promNetworkAddr + ", statisNetworkAddr=" + statisNetworkAddr + ", updateNetworkAddr=" + updateNetworkAddr + "]";
  }

  public boolean isEmpty() {
    if (promNetworkAddr == null || statisNetworkAddr == null || updateNetworkAddr == null) {
      return true;
    }
    return false;
  }
  public synchronized static Session getInstance() {
    if (session == null) {
      session = new Session();
    }
    return session;
  }
  public String getUUId() {
    return UUId;
  }
}
