package com.gzw.kd.common.utils;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec SM4_Context
 */
public class SM4_Context {
  public int mode;

  public long[] sk;

  public boolean isPadding;

  public SM4_Context() {
    this.mode = 1;
    this.isPadding = true;
    this.sk = new long[32];
  }
}
