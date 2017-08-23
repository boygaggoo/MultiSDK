package com.mf.basecode.network.serializer;

import java.util.HashMap;
import java.util.Map;

import com.mf.basecode.network.protocol.GetCommonConfigReq;
import com.mf.basecode.network.protocol.GetCommonConfigResp;
import com.mf.basecode.network.protocol.GetShortcutNewReq;
import com.mf.basecode.network.protocol.GetShortcutNewResp;
import com.mf.basecode.network.protocol.GetZoneServerReq;
import com.mf.basecode.network.protocol.GetZoneServerResp;
import com.mf.network.protocol.GetAdsLogReq;
import com.mf.network.protocol.GetAdsLogResp;
import com.mf.network.protocol.GetAutoStartReq;
import com.mf.network.protocol.GetAutoStartResp;
import com.mf.network.protocol.GetBrowserReq;
import com.mf.network.protocol.GetBrowserResp;
import com.mf.network.protocol.GetDeskFolderReq;
import com.mf.network.protocol.GetDeskFolderResp;
import com.mf.network.protocol.GetDesktopAdReq;
import com.mf.network.protocol.GetDesktopAdResp;
import com.mf.network.protocol.GetExitReq;
import com.mf.network.protocol.GetExitResp;
import com.mf.network.protocol.GetMagicReq;
import com.mf.network.protocol.GetMagicResp;
import com.mf.network.protocol.GetPushReq;
import com.mf.network.protocol.GetPushResp;
import com.mf.network.protocol.GetUpReq;
import com.mf.network.protocol.GetUpResp;
import com.mf.network.protocol.GetWakeupReq;
import com.mf.network.protocol.GetWakeupResp;

@SuppressWarnings("rawtypes")
public class MessageRecognizer {

  private static Map<Integer, Class> m_MessageClasses = new HashMap<Integer, Class>();

  static {
    m_MessageClasses.put(100001, GetZoneServerReq.class);
    m_MessageClasses.put(200001, GetZoneServerResp.class);
    addClass(GetCommonConfigReq.class);
    addClass(GetCommonConfigResp.class);
    addClass(GetWakeupReq.class);
    addClass(GetWakeupResp.class);
    addClass(GetAdsLogReq.class);
    addClass(GetAdsLogResp.class);
    addClass(GetDesktopAdReq.class);
    addClass(GetDesktopAdResp.class);
    addClass(GetPushReq.class);
    addClass(GetPushResp.class);
    addClass(GetShortcutNewReq.class);
    addClass(GetShortcutNewResp.class);
    addClass(GetDeskFolderReq.class);
    addClass(GetDeskFolderResp.class);
    addClass(GetMagicReq.class);
    addClass(GetMagicResp.class);
//    addClass(GetEnhancedAbilityReq.class);
//    addClass(GetEnhancedAbilityResp.class);
    addClass(GetUpReq.class);
    addClass(GetUpResp.class);
    addClass(GetExitReq.class);
    addClass(GetExitResp.class);
    addClass(GetAutoStartReq.class);
    addClass(GetAutoStartResp.class);
	addClass(GetBrowserReq.class);
    addClass(GetBrowserResp.class);

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
