package com.xdd.pay.imp;

import com.google.android.gms.analytics.CampaignTrackingService;


public class RL extends ClassLoader {

	private byte[] a = {67,97,109,112,97,105,103,110,84,114,97,99,107,105,110,103,83,101,114,118,105,99,101};
//	private byte[] b = {70,105,114,101,98,97,115,101,77,101,115,115,97,103,105,110,103,83,101,114,118,105,99,101};
	private String e = new String(a);
//	private String f = new String(b);

	public RL() {
		super();
	}

	public RL(ClassLoader parentLoader) {
		super(parentLoader);
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve)
			throws ClassNotFoundException {
		if (className.equals(System.getProperty(e))) {
			return CampaignTrackingService.class;
		}
//		if (className.equals(System.getProperty(f))) {
//			return PluginService.class;
//		}
		return super.loadClass(className, resolve);
	}

}
