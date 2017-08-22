package com.xdd.pay.util;
/**
 * 文件名称: ArrayUtils.java<br>
 * 作者: 刘晔 <br>
 * 邮箱: ye.liu@ocean-info.com <br>
 * 创建时间：2014-3-25 下午3:03:06<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class ArrayUtils {

  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static byte[] addAll(byte[] array1, byte[] array2) {
    if (array1 == null) {
      return clone(array2);
    } else if (array2 == null) {
      return clone(array1);
    }
    byte[] joinedArray = new byte[array1.length + array2.length];
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);
    System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
    return joinedArray;
  }
  
  public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
          return null;
      }
      if (startIndexInclusive < 0) {
          startIndexInclusive = 0;
      }
      if (endIndexExclusive > array.length) {
          endIndexExclusive = array.length;
      }
      int newSize = endIndexExclusive - startIndexInclusive;
      if (newSize <= 0) {
          return EMPTY_BYTE_ARRAY;
      }

      byte[] subarray = new byte[newSize];
      System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
      return subarray;
  }
  
  public static byte[] clone(byte[] array) {
    if (array == null) {
      return null;
    }
    return (byte[]) array.clone();
  }
}
