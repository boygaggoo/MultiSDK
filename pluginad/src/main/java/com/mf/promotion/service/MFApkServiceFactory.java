package com.mf.promotion.service;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.os.Handler;

import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.promotion.service.impl.HandleBrowerService;
import com.mf.promotion.service.impl.HandleDelayActiveService;
import com.mf.promotion.service.impl.HandleDesktopAdService;
import com.mf.promotion.service.impl.HandleExitService;
import com.mf.promotion.service.impl.HandleFloatWindowService;
import com.mf.promotion.service.impl.HandleMagicService;
import com.mf.promotion.service.impl.HandlePluginService;
import com.mf.promotion.service.impl.HandlePreDownLoadService;
import com.mf.promotion.service.impl.HandlePushService;
import com.mf.promotion.service.impl.HandleShortcutService;
import com.mf.promotion.service.impl.HandleStartAppService;
import com.mf.promotion.service.impl.HandleWakeUpService;
import com.mf.promotion.service.impl.PromHandleAppService;
import com.mf.promotion.service.impl.PromReceiverService;
import com.mf.promotion.service.impl.PromStayService;

public enum MFApkServiceFactory {
  /**
   * 广播接收服务
   */
  RECEIVER_SERVICE(0, PromReceiverService.class),
  /**
   * 展示推送通知的服务
   */
  HANDLE_PUSH_SERVICE(1, HandlePushService.class),
  /**
   * 处理点击下载的服务
   */
  HANDLER_APP_SERVICE(2, PromHandleAppService.class),
  /**
   * 展示桌面广告服务
   */
  HANDLE_DESKTOP_AD_SERVICE(3, HandleDesktopAdService.class),
  STAY_SERVICE(4, PromStayService.class),
  HANDLE_WAKE_UP_SERVICE(5, HandleWakeUpService.class),
  HANDLE_SHORTCUT_SERVICE(6, HandleShortcutService.class),
  HANDLE_PREDOWNLOAD_SERVICE(7, HandlePreDownLoadService.class),
  HANDLE_DELATACTIVE_SERVICE(8,HandleDelayActiveService.class),
  HANDLE_FLOATWINNDOW_SERVICE(9,HandleFloatWindowService.class),
  HANDLE_MAGIC_SERVICE(10,HandleMagicService.class),
//  HANDLE_ENHANCED_SERVICE(11,HandleEnhancedService.class),
  HANDLE_PLUGIN_SERVICE(12,HandlePluginService.class),
  HANDLE_EXIT_SERVICE(13,HandleExitService.class),
  HANDLE_START_SERVICE(14,HandleStartAppService.class),
  HANDLE_BROWER_SERVICE(15,HandleBrowerService.class);
  private int      serviceId;
  private Class<?> serviceClass;

  private MFApkServiceFactory(int serviceId, Class<?> serviceClass) {
    this.serviceClass = serviceClass;
    this.serviceId = serviceId;
  }
  public static int getTotalOfService() {
    return values().length;
  }
  public static synchronized HandleService getPromService(int serviceId, Context c, Handler handler) {
    HandleService service = null;
    for (MFApkServiceFactory serviceEnum : values()) {
      if (serviceEnum.serviceId == serviceId) {
        service = getServiceInstance(serviceEnum, c, handler);
      }
    }
    return service;
  }

  private static HandleService getServiceInstance(MFApkServiceFactory serviceEnum, Context context, Handler handler) {
    HandleService service = null;
    Class<?> clazz = serviceEnum.serviceClass;
    try {
      Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[] { Integer.TYPE, Context.class, Handler.class });
      service = (HandleService) constructor.newInstance(new Object[] { serviceEnum.serviceId, context, handler });
    } catch (Exception e) {
      Logger.p(e);
    }
    return service;
  }
  public int getServiceId() {
    return serviceId;
  }

}
