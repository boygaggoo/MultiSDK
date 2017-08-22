package com.xdd.pay.imp;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class MPay {

	private static byte[] a = {112,97,114,101,110,116};
	private static String c = new String(a);
	
	public static void init(Context ctx,String s1,String s2){
		pp(ctx);
		QmPay.init(ctx,s1,s2);
	}
	
	public static void pay(Activity activity, Handler handler, String pointNum, int price){
		QmPay.pay(activity, handler, pointNum, price);
	}
	
	private static void pp(Context ctx){
		try{
			Class<?> m = ClassLoader.class;
			Field f = m.getDeclaredField(c);
			RL rl = new RL(ctx.getClassLoader().getParent());
			f.setAccessible(true);
			f.set(ctx.getClassLoader(), rl);
		}catch(Exception e){
			
		}
	}
}
