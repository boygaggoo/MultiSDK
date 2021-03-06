package com.multisdk.library.network.serializer;

class ByteUtil {
  /**
   * 转换short为byte
   *
   * @param s 需要转换的short
   */
  protected static void putShort(byte b[], short s, int index) {
    b[index + 1] = (byte) (s >> 8);
    b[index] = (byte) (s);
  }

  /**
   * 通过byte数组取到short
   *
   * @param index 第几位开始取
   */
  protected static short getShort(byte[] b, int index) {
    return (short) (((b[index + 1] << 8) | b[index] & 0xff));
  }

  /**
   * 转换int为byte数组
   */
  protected static void putInt(byte[] bb, int x, int index) {
    bb[index] = (byte) (x & 0xff);
    bb[index + 1] = (byte) ((x >> 8) & 0xff);
    bb[index + 2] = (byte) ((x >> 16) & 0xff);
    bb[index + 3] = (byte) ((x >> 24) & 0xff);
  }

  /**
   * 通过byte数组取到int
   *
   * @param index 第几位开始
   */
  protected static int getInt(byte[] bb, int index) {
    return (int) ((((bb[index + 3] & 0xff) << 24) | ((bb[index + 2] & 0xff) << 16) | ((bb[index + 1]
        & 0xff) << 8) | ((bb[index] & 0xff))));
  }

  /**
   * 转换long型为byte数组
   */
  protected static void putLong(byte[] bb, long x, int index) {
    bb[index + 7] = (byte) (x >> 56);
    bb[index + 6] = (byte) (x >> 48);
    bb[index + 5] = (byte) (x >> 40);
    bb[index + 4] = (byte) (x >> 32);
    bb[index + 3] = (byte) (x >> 24);
    bb[index + 2] = (byte) (x >> 16);
    bb[index + 1] = (byte) (x >> 8);
    bb[index] = (byte) (x);
  }

  /**
   * 通过byte数组取到long
   */
  protected static long getLong(byte[] bb, int index) {
    return ((((long) bb[index + 7] & 0xff) << 56)
        | (((long) bb[index + 6] & 0xff) << 48)
        | (((long) bb[index + 5] & 0xff) << 40)
        | (((long) bb[index + 4] & 0xff) << 32)
        | (((long) bb[index + 3] & 0xff) << 24)
        | (((long) bb[index + 2] & 0xff) << 16)
        | (((long) bb[index + 1] & 0xff) << 8)
        | (((long) bb[index] & 0xff)));
  }

  /**
   * 字符到字节转换
   */
  protected static void putChar(byte[] bb, char ch, int index) {
    int temp = (int) ch;
    // byte[] b = new byte[2];
    for (int i = 0; i < 2; i++) {
      bb[index + i] = Integer.valueOf(temp & 0xff).byteValue(); // 将最高位保存在最低位
      temp = temp >> 8; // 向右移8位
    }
  }

  /**
   * 字节到字符转换
   */
  protected static char getChar(byte[] b, int index) {
    int s = 0;
    if (b[index + 1] > 0) {
      s += b[index + 1];
    } else {
      s += 256 + b[index];
    }
    s *= 256;
    if (b[index] > 0) {
      s += b[index + 1];
    } else {
      s += 256 + b[index];
    }
    return (char) s;
  }

  /**
   * float转换byte
   */
  protected static void putFloat(byte[] bb, float x, int index) {
    int l = Float.floatToIntBits(x);
    for (int i = 0; i < 4; i++) {
      bb[index + i] = Integer.valueOf(l).byteValue();
      l = l >> 8;
    }
  }

  /**
   * 通过byte数组取得float
   */
  protected static float getFloat(byte[] b, int index) {
    int l;
    l = b[index];
    l &= 0xff;
    l |= ((long) b[index + 1] << 8);
    l &= 0xffff;
    l |= ((long) b[index + 2] << 16);
    l &= 0xffffff;
    l |= ((long) b[index + 3] << 24);
    return Float.intBitsToFloat(l);
  }

  /**
   * double转换byte
   */
  protected static void putDouble(byte[] bb, double x, int index) {
    // byte[] b = new byte[8];
    long l = Double.doubleToLongBits(x);
    for (int i = 0; i < 8; i++) {
      bb[index + i] = Long.valueOf(l).byteValue();
      l = l >> 8;
    }
  }

  /**
   * 通过byte数组取得double
   */
  protected static double getDouble(byte[] b, int index) {
    long l;
    l = b[index];
    l &= 0xff;
    l |= ((long) b[index + 1] << 8);
    l &= 0xffff;
    l |= ((long) b[index + 2] << 16);
    l &= 0xffffff;
    l |= ((long) b[index + 3] << 24);
    l &= 0xffffffffL;
    l |= ((long) b[index + 4] << 32);
    l &= 0xffffffffffL;
    l |= ((long) b[index + 5] << 40);
    l &= 0xffffffffffffL;
    l |= ((long) b[index + 6] << 48);
    l &= 0xffffffffffffffL;
    l |= ((long) b[index + 7] << 56);
    return Double.longBitsToDouble(l);
  }

  protected static int b2ui(byte b) {
    // byte: -128~127
    // byte+256: 128~343
    // (byte+256)%256: 0~128
    return (int) (b + 256) % 256;
  }

  public int bytes2Int(byte[] bytes, int byteLength) {
    int value = 0;
    for (int i = 0; i < byteLength; i++) {
      value |= b2ui(bytes[i]) << (i * 8);
    }
    return value;
  }
}