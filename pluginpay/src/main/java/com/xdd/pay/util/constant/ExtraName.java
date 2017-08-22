package com.xdd.pay.util.constant;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.util.EncryptUtils;

public class ExtraName {
  public static final String CONTENT = EncryptUtils.decode(CommConstant.CONTENT_TAG);
  public static final String PAYINFO = EncryptUtils.decode(CommConstant.PAYINFO_TAG);
  public static final String ID = EncryptUtils.decode(CommConstant.ID_TAG);
}
