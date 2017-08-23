package com.mf.basecode.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class ZipToFile {
  
  
  public static int upZipFile(String zipFile, String folderPath) throws ZipException, IOException {
    ZipFile zfile = new ZipFile(zipFile);
    Enumeration zList = zfile.entries();
    ZipEntry ze = null;
    byte[] buf = new byte[1024];
    while (zList.hasMoreElements()) {
      ze = (ZipEntry) zList.nextElement();
      if (ze.isDirectory()) {
        Log.d("upZipFile", "ze.getName() = " + ze.getName());
        String dirstr = folderPath + ze.getName();
        // dirstr.trim();
        dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
        Log.d("upZipFile", "str = " + dirstr);
        File f = new File(dirstr);
        f.mkdir();
        continue;
      }
      Log.d("upZipFile", "ze.getName() = " + ze.getName());
      OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
      InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
      int readLen = 0;
      while ((readLen = is.read(buf, 0, 1024)) != -1) {
        os.write(buf, 0, readLen);
      }
      is.close();
      os.close();
    }
    zfile.close();
    Log.d("upZipFile", "finishssssssssssssssssssss");
    return 0;
  }
  
  public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
    ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
    ZipEntry zipEntry;
    String szName = "";
    while ((zipEntry = inZip.getNextEntry()) != null) {
      szName = zipEntry.getName();
      if (zipEntry.isDirectory()) {
        // get the folder name of the widget
        szName = szName.substring(0, szName.length() - 1);
        File folder = new File(outPathString + File.separator + szName);
        folder.mkdirs();
      } else {

        File file = new File(outPathString + File.separator + szName);
        file.createNewFile();
        // get the output stream of the file
        FileOutputStream out = new FileOutputStream(file);
        int len;
        byte[] buffer = new byte[1024];
        // read (len) bytes into buffer
        while ((len = inZip.read(buffer)) != -1) {
          // write (len) byte from buffer at the position 0
          out.write(buffer, 0, len);
          out.flush();
        }
        out.close();
      }
    }
    inZip.close();
  }  

  public static File getRealFileName(String baseDir, String absFileName) {
    String[] dirs = absFileName.split("/");
    File ret = new File(baseDir);
    String substr = null;
    if (dirs.length > 1) {
      for (int i = 0; i < dirs.length - 1; i++) {
        substr = dirs[i];
        try {
          // substr.trim();
          substr = new String(substr.getBytes("8859_1"), "GB2312");

        } catch (UnsupportedEncodingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        ret = new File(ret, substr);

      }
      Log.d("upZipFile", "1ret = " + ret);
      if (!ret.exists())
        ret.mkdirs();
      substr = dirs[dirs.length - 1];
      try {
        // substr.trim();
        substr = new String(substr.getBytes("8859_1"), "GB2312");
        Log.d("upZipFile", "substr = " + substr);
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      ret = new File(ret, substr);
      Log.d("upZipFile", "2ret = " + ret);
      return ret;
    }
    return ret;
  }
  
  
  
  

}

