package com.zxing.widget;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class CustomToast {
	private static int delayTime = 2000;
	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	public static void showToast(Context mContext, String text) {
		if(null != text){
			mHandler.removeCallbacks(r);
			if (mToast != null)
				mToast.setText(text);
			else
				mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
			mHandler.postDelayed(r, delayTime);
	
			mToast.show();
		}
	}

	public static void showToast(Context mContext, int resId) {
		showToast(mContext, mContext.getResources().getString(resId));
	}
	
	public static void showToast(Context mContext, String text, int duration) {
		if(null != text){
			mHandler.removeCallbacks(r);
			if (mToast != null)
				mToast.setText(text);
			else
				mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
			mHandler.postDelayed(r, 2000);
	
			mToast.show();
		}
	}

	public static void showToast(Context mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}
}
