package com.xdd.pay.util;

import java.util.Random;

public class MathUtils {
  /**
   * 获取指定长度的随即字符串
   * 
   * @param i
   * @return
   */
  public static String getRandomString(int i) {
    String ret = "";
    if (i == 0) {
      return ret;
    }
    Random random = new Random();
    int count = 0;
    while (true) {
      if (i - 10 > 0) {
        i -= 10;
        count = 10;
        ret += getRandomString(random, count);
      } else {
        count = i;
        ret += getRandomString(random, count);
        break;
      }
    }
    return ret;
  }

  /**
   * 
   * @param random
   * @param count
   * @return
   */
  private static String getRandomString(Random random, int count) {
    int max = (int) Math.pow(10, count) - 1;
    int min = (int) Math.pow(10, count - 1);
    return random.nextInt(max) % (max - min + 1) + min + "";
  }
}
