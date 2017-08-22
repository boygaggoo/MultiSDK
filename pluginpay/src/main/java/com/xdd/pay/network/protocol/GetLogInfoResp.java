package com.xdd.pay.network.protocol;

import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetLogInfoResp.java<br>
 * 作者: hbx<br>
 * 创建时间：2014-5-21 17:30:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201003,encrypt=true)
public class GetLogInfoResp extends ResponseBody {

  private static final long serialVersionUID = 4689141913252847347L;

}
