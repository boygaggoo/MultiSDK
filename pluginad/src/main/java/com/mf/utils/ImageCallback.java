package com.mf.utils;

import android.graphics.drawable.Drawable;

import com.mf.promotion.widget.CircleProgressView;

public interface ImageCallback {

	void imageLoaded(CircleProgressView imageview,Drawable  drawable, String name);

}
