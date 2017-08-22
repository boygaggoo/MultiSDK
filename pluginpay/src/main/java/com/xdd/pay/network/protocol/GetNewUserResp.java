package com.xdd.pay.network.protocol;

import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetNewUserResp.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 17:30:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201001,encrypt=true)
public class GetNewUserResp extends ResponseBody {

  private static final long serialVersionUID = -5919381850915656994L;

}
