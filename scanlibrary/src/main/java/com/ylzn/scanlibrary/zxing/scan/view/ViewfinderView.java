/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ylzn.scanlibrary.zxing.scan.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.ylzn.scanlibrary.R;
import com.ylzn.scanlibrary.zxing.scan.camera.CameraManager;
import com.ylzn.scanlibrary.zxing.scan.decoding.Constants;


import java.util.Collection;
import java.util.HashSet;

/**
 * 修改页面，以及自定义页面
 * 
 */
public  final class ViewfinderView extends View {
	private static final String TAG = "log";
	/**
	 * 刷新界面的时间 
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个绿色边角对应的长度 
	 */
	private int ScreenRate;
	
	/**
	 * 四个绿色边角对应的宽度 
	 */
	private static final int CORNER_WIDTH = 10;
	/**
	 * 扫描框中的中间线的宽度 
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	
	/**
	 * 扫描框中的中间线的与扫描框左右的间隙 
	 */
	private static final int MIDDLE_LINE_PADDING = 5;
	
	/**
	 * 中间那条线每次刷新移动的距离 
	 */
	private static final int SPEEN_DISTANCE = 5;
	
	/**
	 * 手机的屏幕密度 
	 */
	private static float density;
	/**
	 * 字体大小 
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离 
	 */
	private static final int TEXT_PADDING_TOP = 30;
	
	/**
	 * 画笔对象的引用 
	 */
	private Paint paint;
	
	/**
	 * 中间滑动线的最顶端位置 
	 */
	private int slideTop;
	
	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;
	
	private Bitmap resultBitmap;
	private  int maskColor;
	private  int resultColor;
	
	//private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	//手电筒相关
	private Bitmap flashLightBitmap;
	private Bitmap openFlashLightBitmap;
	private Bitmap scanLineBitmap;
	private String flashLightOpenText;
	private String flashLightCloseText;
	private Paint flashLightTextPaint;
	private boolean isOpenFlashLight;
	private int UNIT = 50;
	private int TEXT_UNIT = 40;
	private float flashLightBottomDistance;
	private float flashTextBottomDistance;
	private Rect flashRect;
	private Rect flashOpenRect;
	private Rect frame;
	private onFlashLightStateChangeListener mOnFlashLightStateChangeListener;

	boolean isFirst;

	public ViewfinderView(Context context) {
		super(context);
	}

	public ViewfinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		density = context.getResources().getDisplayMetrics().density;
		//将像素转换成dp  
		ScreenRate = (int)(20 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		flashLightBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.scan_flashlight)).getBitmap();
		openFlashLightBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.scan_open_flashlight)).getBitmap();
		flashRect = new Rect();
		flashOpenRect = new Rect();
		flashLightOpenText = resources.getString(R.string.open_flash_light);
		flashLightCloseText = resources.getString(R.string.close_flash_light);
		flashLightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		flashLightTextPaint.setColor(Color.WHITE);
		flashLightTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE * density, context.getResources().getDisplayMetrics()));
		flashLightBottomDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, UNIT * density, context.getResources().getDisplayMetrics());
		flashTextBottomDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, TEXT_UNIT * density, context.getResources().getDisplayMetrics());

		//扫码时出现的黄点
		//resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		//中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		
		//初始化中间线滑动的最上边和最下边
		if(!isFirst){
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}
		
		//获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		
		//画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面  
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边  
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);
		
		
		
		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			//画扫描框边上的角，总共8个部分
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate,
					frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
					frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
					frame.right, frame.bottom, paint);

			
			//绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if(slideTop >= frame.bottom){
				slideTop = frame.top;
			}
			canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);
			
			
			//画扫描框下面的字
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			int iScanTextWidth = (int) paint.measureText(getResources().getString(R.string.scan_text));
			int iScanTextLeft = (width - iScanTextWidth) / 2;
			canvas.drawText(getResources().getString(R.string.scan_text), iScanTextLeft, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);

			//手电筒关闭时
			if (Constants.isWeakLight) {

				if (!isOpenFlashLight) {
					flashRect.left = (width - flashLightBitmap.getWidth()) / 2;
					flashRect.right = (width + flashLightBitmap.getWidth()) / 2;
					flashRect.bottom = (int) (frame.bottom - flashLightBottomDistance);
					flashRect.top = (int) (frame.bottom - flashLightBottomDistance - flashLightBitmap.getHeight());
					canvas.drawBitmap(flashLightBitmap, null, flashRect, paint);

					Rect flashTextRect = new Rect();
					flashTextRect.left = (int) ((width - flashLightTextPaint.measureText(flashLightOpenText)) / 2);
					flashTextRect.right = (int) ((width + flashLightTextPaint.measureText(flashLightOpenText)) / 2);
					flashTextRect.bottom = (int) (frame.bottom - flashTextBottomDistance);
					flashTextRect.top = flashTextRect.bottom - 5;
					canvas.drawText(flashLightOpenText, flashTextRect.left, flashRect.bottom + 50, flashLightTextPaint);
				}
			}

			//手电筒为开启时
			if (isOpenFlashLight) {
				flashOpenRect.left = (width - openFlashLightBitmap.getWidth()) / 2;
				flashOpenRect.right = (width + openFlashLightBitmap.getWidth()) / 2;
				flashOpenRect.bottom = (int) (frame.bottom - flashLightBottomDistance);
				flashOpenRect.top = (int) (frame.bottom - flashLightBottomDistance - openFlashLightBitmap.getHeight());
				canvas.drawBitmap(openFlashLightBitmap, null, flashOpenRect, paint);

				Rect flashOpenTextRect = new Rect();
				flashOpenTextRect.left = (int) ((width - flashLightTextPaint.measureText(flashLightCloseText)) / 2);
				flashOpenTextRect.right = (int) ((width + flashLightTextPaint.measureText(flashLightCloseText)) / 2);
				flashOpenTextRect.bottom = (int) (frame.bottom - flashTextBottomDistance);
				flashOpenTextRect.top = flashOpenTextRect.bottom - 5;
				canvas.drawText(flashLightCloseText, flashOpenTextRect.left, flashOpenRect.bottom + 50, flashLightTextPaint);

			}



			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				//paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				//paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			
			//只刷新扫描框的内容，其他地方不刷新 
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
			
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();
		if (!isOpenFlashLight) {
			isOpenFlashLight = openFlashLight(x, y);
		} else {
			isOpenFlashLight = false;
		}
		if (null != mOnFlashLightStateChangeListener) {

			mOnFlashLightStateChangeListener.openFlashLight(isOpenFlashLight);
		}

		return super.onTouchEvent(event);
	}

    //是否触摸到闪光灯
	private boolean openFlashLight(int x, int y) {
	    //加大范围
		Rect rect = flashRect;
		rect.left = flashRect.left - 10;
		rect.right = flashRect.right + 10;
		rect.top = flashRect.top - 10;
		rect.bottom = flashRect.bottom + 10;
		return rect.contains(x, y);
	}

	public void reOnDraw() {
		if (null != frame) {
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
	}

	public interface onFlashLightStateChangeListener {
		void openFlashLight(boolean open);
	}

	public void setOnFlashLightStateChangeListener(onFlashLightStateChangeListener onFlashLightStateChangeListener) {
		mOnFlashLightStateChangeListener = onFlashLightStateChangeListener;
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
