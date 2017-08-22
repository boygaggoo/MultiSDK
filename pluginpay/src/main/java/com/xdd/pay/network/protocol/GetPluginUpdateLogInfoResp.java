package com.xdd.pay.network.protocol;

import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * @author wander
 * @data 2015-3-25
 */
@SignalCode(messageCode = 201010, encrypt = true)
public class GetPluginUpdateLogInfoResp extends ResponseBody {

	private static final long	serialVersionUID	= 8162947137679477493L;

}