package com.xdd.pay.network.serializer;

import java.util.HashMap;
import java.util.Map;

import com.xdd.pay.network.protocol.GetExceptionLogInfoResp;
import com.xdd.pay.network.protocol.GetInitResp;
import com.xdd.pay.network.protocol.GetLogInfoResp;
import com.xdd.pay.network.protocol.GetNetGameResp;
import com.xdd.pay.network.protocol.GetNewUserResp;
import com.xdd.pay.network.protocol.GetPayCodeResp;
import com.xdd.pay.network.protocol.GetPayInfoResp;
import com.xdd.pay.network.protocol.GetPluginUpdateLogInfoResp;
import com.xdd.pay.network.protocol.GetSmsReceiverLogInfoResp;
import com.xdd.pay.network.protocol.GetSmsResp;
import com.xdd.pay.network.protocol.GetTaskResp;
import com.xdd.pay.network.protocol.GetWapPayInfoResp;
import com.xdd.pay.network.protocol.GetWebPayInfoResp;
import com.xdd.pay.network.protocol.SdkPluginResp;
import com.xdd.pay.network.protocol.ThirdSdkPluginResp;

@SuppressWarnings("rawtypes")
public class MessageRecognizer {

	private static Map<Integer, Class> m_MessageClasses = new HashMap<Integer, Class>();

	static {
		m_MessageClasses.put(201001, GetNewUserResp.class);
		m_MessageClasses.put(201002, GetInitResp.class);
		m_MessageClasses.put(201003, GetLogInfoResp.class);
		m_MessageClasses.put(201004, GetPayCodeResp.class);
		m_MessageClasses.put(201005, GetPayInfoResp.class);
		m_MessageClasses.put(201006, GetWebPayInfoResp.class);
		m_MessageClasses.put(201007, GetWapPayInfoResp.class);
		m_MessageClasses.put(201008, GetNetGameResp.class);
		m_MessageClasses.put(201009, SdkPluginResp.class);
		m_MessageClasses.put(201010, GetPluginUpdateLogInfoResp.class);
		m_MessageClasses.put(201012, GetSmsResp.class);
		m_MessageClasses.put(201013, GetSmsReceiverLogInfoResp.class);
		m_MessageClasses.put(201014, GetExceptionLogInfoResp.class);
		m_MessageClasses.put(201015, ThirdSdkPluginResp.class);
		m_MessageClasses.put(201016, GetTaskResp.class);
//		m_MessageClasses.put(201017, CmccSdkResp.class);
	}

	public static Class getClassByCode(int code) {
		if (m_MessageClasses.containsKey(code)) {
			return m_MessageClasses.get(code);
		}
		return null;
	}

	public static boolean addClass(Class cls) {
		SignalCode sc = AttributeUitl.getMessageAttribute(cls);
		if (sc != null) {
			if (!m_MessageClasses.containsKey(sc.messageCode())) {
				m_MessageClasses.put(sc.messageCode(), cls);
				return true;
			}
		}
		return false;
	}
}
