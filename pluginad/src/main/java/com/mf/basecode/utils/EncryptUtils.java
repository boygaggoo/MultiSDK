package com.mf.basecode.utils;

import java.security.MessageDigest;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mf.basecode.config.MFSDKConfig;
import com.mf.basecode.utils.contants.CommConstants;

public class EncryptUtils {

  public static final byte[] MF_DEVELOP_NETWORK_ADDR /*
                                                      * http://zt917.tpddns.cn :3100/
                                                      */= {(byte) 0x61, (byte) 0x48, (byte) 0x52,
      (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x33,
      (byte) 0x70, (byte) 0x30, (byte) 0x4f, (byte) 0x54, (byte) 0x45, (byte) 0x33, (byte) 0x4c,
      (byte) 0x6e, (byte) 0x52, (byte) 0x77, (byte) 0x5a, (byte) 0x47, (byte) 0x52, (byte) 0x75,
      (byte) 0x63, (byte) 0x79, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x6a, (byte) 0x6f,
      (byte) 0x7a, (byte) 0x4d, (byte) 0x54, (byte) 0x41, (byte) 0x77, (byte) 0x4c, (byte) 0x77,
      (byte) 0x3d, (byte) 0x3d,};

  public static final byte[] MF_DEVELOP_CRTPT_KEY = {(byte) 0x65, (byte) 0x6d, (byte) 0x74,
      (byte) 0x66, (byte) 0x4d, (byte) 0x6c, (byte) 0x39, (byte) 0x33, (byte) 0x5a, (byte) 0x57,
      (byte) 0x74, (byte) 0x6b,}; // zk_2_wekd

  public static final byte[] MF_RELEASE_NETWORK_ADDR = {(byte) 0x61, (byte) 0x48, (byte) 0x52,
      (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x32,
      (byte) 0x52, (byte) 0x76, (byte) 0x62, (byte) 0x57, (byte) 0x46, (byte) 0x70, (byte) 0x62,
      (byte) 0x69, (byte) 0x35, (byte) 0x36, (byte) 0x64, (byte) 0x44, (byte) 0x6b, (byte) 0x78,
      (byte) 0x4e, (byte) 0x69, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x32, (byte) 0x30,
      (byte) 0x36, (byte) 0x4d, (byte) 0x7a, (byte) 0x45, (byte) 0x77, (byte) 0x4d, (byte) 0x43,
      (byte) 0x38, (byte) 0x3d,};// http://domain.zt916.com:3100/
  public static final byte[] MF_RELEASE_CRTPT_KEY = {(byte) 0x65, (byte) 0x6e, (byte) 0x52,
      (byte) 0x6e, (byte) 0x5a, (byte) 0x31, (byte) 0x38, (byte) 0x34, (byte) 0x64, (byte) 0x32,
      (byte) 0x6f, (byte) 0x3d,};// ztgg_8wj

  public static final byte[] MF_RELEASE_NETWORK_SECOND_ADDR/*
                                                            * http://dm.daguolia .com:3100/
                                                            */= {(byte) 0x61, (byte) 0x48,
      (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c,
      (byte) 0x32, (byte) 0x52, (byte) 0x74, (byte) 0x4c, (byte) 0x6d, (byte) 0x52, (byte) 0x68,
      (byte) 0x5a, (byte) 0x33, (byte) 0x56, (byte) 0x76, (byte) 0x62, (byte) 0x47, (byte) 0x6c,
      (byte) 0x68, (byte) 0x4c, (byte) 0x6d, (byte) 0x4e, (byte) 0x76, (byte) 0x62, (byte) 0x54,
      (byte) 0x6f, (byte) 0x7a, (byte) 0x4d, (byte) 0x54, (byte) 0x41, (byte) 0x77, (byte) 0x4c,
      (byte) 0x77, (byte) 0x3d, (byte) 0x3d,};

  private final static byte[] MF_RELEASE_NETWORK_THRID_ADDR = {(byte) 0x61, (byte) 0x48,
      (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c,
      (byte) 0x32, (byte) 0x52, (byte) 0x76, (byte) 0x62, (byte) 0x57, (byte) 0x46, (byte) 0x70,
      (byte) 0x62, (byte) 0x69, (byte) 0x35, (byte) 0x36, (byte) 0x64, (byte) 0x44, (byte) 0x6b,
      (byte) 0x78, (byte) 0x4e, (byte) 0x69, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x32,
      (byte) 0x30, (byte) 0x36, (byte) 0x4d, (byte) 0x7a, (byte) 0x45, (byte) 0x77, (byte) 0x4d,
      (byte) 0x43, (byte) 0x38, (byte) 0x3d,};// http://domain.zt916.com:3100/

  private final static byte[] MF_DEBUG_FILE_NAME = {(byte) 0x61, (byte) 0x47, (byte) 0x70,
      (byte) 0x48, (byte) 0x64, (byte) 0x47, (byte) 0x74, (byte) 0x70, (byte) 0x4c, (byte) 0x6d,
      (byte) 0x52, (byte) 0x69, (byte) 0x64, (byte) 0x57, (byte) 0x63, (byte) 0x3d,}; // hjGtki.dbug

  private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1,
      -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
      20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
      37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};

  public static String getUnKey(final byte[] pArray) {
    if (pArray == null || pArray.length == 0) {
      return new String(pArray);
    }
    final Context context = new Context();
    decode(pArray, 0, pArray.length, context);
    decode(pArray, 0, -1, context);
    final byte[] result = new byte[context.pos];
    readResults(result, 0, result.length, context);
    return new String(result);
  }

  private static void decode(final byte[] in, int inPos, final int inAvail, final Context context) {
    if (context.eof) {
      return;
    }
    int MASK_8BITS = 0xff;
    if (inAvail < 0) {
      context.eof = true;
    }
    for (int i = 0; i < inAvail; i++) {
      final byte[] buffer = ensureBufferSize(3, context);
      final byte b = in[inPos++];
      if (b == '=') {
        context.eof = true;
        break;
      } else {
        if (b >= 0 && b < DECODE_TABLE.length) {
          final int result = DECODE_TABLE[b];
          if (result >= 0) {
            context.modulus = (context.modulus + 1) % 4;
            context.ibitWorkArea = (context.ibitWorkArea << 6) + result;
            if (context.modulus == 0) {
              buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 16) & MASK_8BITS);
              buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
              buffer[context.pos++] = (byte) (context.ibitWorkArea & MASK_8BITS);
            }
          }
        }
      }
    }

    if (context.eof && context.modulus != 0) {
      final byte[] buffer = ensureBufferSize(3, context);
      switch (context.modulus) {
        case 1:
          break;
        case 2:
          context.ibitWorkArea = context.ibitWorkArea >> 4;
          buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
          break;
        case 3:
          context.ibitWorkArea = context.ibitWorkArea >> 2;
          buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
          buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
          break;
      }
    }
  }

  private static int readResults(final byte[] b, final int bPos, final int bAvail,
      final Context context) {
    if (context.buffer != null) {
      final int len = Math.min(context.pos - context.readPos, bAvail);
      System.arraycopy(context.buffer, context.readPos, b, bPos, len);
      context.readPos += len;
      if (context.readPos >= context.pos) {
        context.buffer = null;
      }
      return len;
    }
    return context.eof ? -1 : 0;
  }

  private static byte[] ensureBufferSize(final int size, final Context context) {
    if ((context.buffer == null) || (context.buffer.length < context.pos + size)) {
      context.buffer = new byte[8192];
      context.pos = 0;
      context.readPos = 0;
    }
    return context.buffer;
  }

  static class Context {
    int ibitWorkArea;
    long lbitWorkArea;
    byte[] buffer;
    int pos;
    int readPos;
    boolean eof;
    int currentLinePos;
    int modulus;
  }

  public static String getMfEncryptKey() {
    if (MFSDKConfig.getInstance().isDebugMode()) {
      return getUnKey(MF_DEVELOP_CRTPT_KEY);
    } else {
      return getUnKey(MF_RELEASE_CRTPT_KEY);
    }
  }

  public static String getMfNetworkAddr(android.content.Context mContext, int retryCount) {
    if (MFSDKConfig.getInstance().isDebugMode()) {
      return getUnKey(MF_DEVELOP_NETWORK_ADDR);
    } else {
      SharedPreferences spf =
          mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
      if (retryCount == 0) {
        String ori0 = spf.getString(CommConstants.SESSION_ORIGIN_ADD + retryCount, "");
        if (TextUtils.isEmpty(ori0) || !ori0.equals(getUnKey(MF_RELEASE_NETWORK_ADDR))) {
          spf.edit()
              .putString(CommConstants.SESSION_ORIGIN_ADD + retryCount,
                  getUnKey(MF_RELEASE_NETWORK_ADDR)).commit();
          return getUnKey(MF_RELEASE_NETWORK_ADDR);
        } else {
          return ori0;
        }
      } else if (retryCount == 1) {
        String ori1 = spf.getString(CommConstants.SESSION_ORIGIN_ADD + retryCount, "");
        if (TextUtils.isEmpty(ori1) || !ori1.equals(getUnKey(MF_RELEASE_NETWORK_SECOND_ADDR))) {
          spf.edit()
              .putString(CommConstants.SESSION_ORIGIN_ADD + retryCount,
                  getUnKey(MF_RELEASE_NETWORK_SECOND_ADDR)).commit();
          return getUnKey(MF_RELEASE_NETWORK_SECOND_ADDR);
        } else {
          return ori1;
        }
      } else if (retryCount == 2) {
        String ori2 = spf.getString(CommConstants.SESSION_ORIGIN_ADD + retryCount, "");
        if (TextUtils.isEmpty(ori2) || !ori2.equals(getUnKey(MF_RELEASE_NETWORK_THRID_ADDR))) {
          spf.edit()
              .putString(CommConstants.SESSION_ORIGIN_ADD + retryCount,
                  getUnKey(MF_RELEASE_NETWORK_THRID_ADDR)).commit();
          return getUnKey(MF_RELEASE_NETWORK_THRID_ADDR);
        } else {
          return ori2;
        }
      }

      return "";
    }
  }

  public static String getMfDebugFileName() {
    return getUnKey(MF_DEBUG_FILE_NAME);
  }

  public static String getEncrypt(String content) {
    String result = "";
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(content.getBytes());
      byte buffer[] = md.digest();
      StringBuffer sb = new StringBuffer(buffer.length * 2);
      for (int i = 0; i < buffer.length; i++) {
        sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
        sb.append(Character.forDigit(buffer[i] & 15, 16));
      }
      result = sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String convertMD5(String inStr) {
    String s = "";
    try {
      char[] a = inStr.toCharArray();
      for (int i = 0; i < a.length; i++) {
        a[i] = (char) (a[i] ^ 't');
      }
      s = new String(a);

    } catch (Exception ignored) {
    }
    return s;
  }

}
