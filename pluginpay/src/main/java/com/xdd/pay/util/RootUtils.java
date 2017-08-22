package com.xdd.pay.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.xdd.pay.constant.CommConstant;

/**
 * 文件名称: RootUtils.java<br>
 * 作者: 刘晔 <br>
 * 邮箱: ye.liu@ocean-info.com <br>
 * 创建时间：2014-1-21 下午1:50:58<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class RootUtils {
  /**
   * 手机是否ROOT
   * 
   * @return
   */
  public static boolean isPhoneRooted() {
    boolean isRoot = false;
    String sys = System.getenv(EncryptUtils.decode(CommConstant.PATH_TAG));
    ArrayList<String> commands = new ArrayList<String>();
    String[] path = sys.split(":");
    for (int i = 0; i < path.length; i++) {
      String commod = "ls -l " + path[i] + EncryptUtils.decode(CommConstant.SU_TAG);
      commands.add(commod);
    }
    ArrayList<String> res = new ArrayList<String>();
    Process process = null;
    BufferedOutputStream shellInput = null;
    BufferedReader shellOutput = null;
    try {
      process = Runtime.getRuntime().exec(EncryptUtils.decode(CommConstant.SYS_PATH_TAG));
      shellInput = new BufferedOutputStream(process.getOutputStream());
      shellOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

      for (String command : commands) {
        shellInput.write((command + " 2>&1\n").getBytes());
      }

      shellInput.write("exit\n".getBytes());
      shellInput.flush();

      String line;
      while ((line = shellOutput.readLine()) != null) {
        res.add(line);
      }

      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      try {
        if (process != null) {
          process.destroy();
        }
        if (null != shellInput) {
        	shellInput.close();
		}
        if (null != shellOutput) {
        	shellOutput.close();
		}
      } catch (Exception e2) {
      }
    }
    String response = "";
    for (int i = 0; i < res.size(); i++) {
      response += res.get(i);
    }
    String root = EncryptUtils.decode(CommConstant.ROOT_CMD_TAG);
    if (response.replaceAll(" ", "").contains(root.replaceAll(" ", ""))) {
      isRoot = true;
    }
    return isRoot;
  }
}
