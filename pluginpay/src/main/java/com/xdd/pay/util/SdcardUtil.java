package com.xdd.pay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;

public class SdcardUtil {

	// 向SD卡写入数据
	public void writeSDcard(String str) {
		try {
			// 判断是否存在SD卡
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// 获取SD卡的目录
				File sdDire = Environment.getExternalStorageDirectory();
				FileOutputStream outFileStream = new FileOutputStream(sdDire.getCanonicalPath() + "/test.txt");
				outFileStream.write(str.getBytes());
				outFileStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 从SD卡中读取数据
	public void readSDcard() {
		StringBuffer strsBuffer = new StringBuffer();
		try {
			// 判断是否存在SD
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/test.txt");
				// 判断是否存在该文件
				if (file.exists()) {
					// 打开文件输入流
					FileInputStream fileR = new FileInputStream(file);
					BufferedReader reads = new BufferedReader(new InputStreamReader(fileR));
					String st = null;
					while ((st = reads.readLine()) != null) {
						strsBuffer.append(st);
					}
					fileR.close();
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  public static String getSdcardid() {
    String memBlk = "";
    String sd_cid = "";
    try {
      File file = new File("/sys/block/mmcblk1");
      if (file.exists() && file.isDirectory()) {
        memBlk = "mmcblk1";
      } else {
        memBlk = "mmcblk0";
      }
      Process cmd = Runtime.getRuntime().exec("cat /sys/block/" + memBlk + "/device/cid");
      BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
      sd_cid = br.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sd_cid;
  }
}
