package com.ylzn.scanlibrary.zxing.scan.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ylzn.scanlibrary.R;
import com.ylzn.scanlibrary.zxing.scan.camera.CameraManager;
import com.ylzn.scanlibrary.zxing.scan.decoding.CaptureActivityHandler;
import com.ylzn.scanlibrary.zxing.scan.decoding.InactivityTimer;
import com.ylzn.scanlibrary.zxing.widget.CustomToast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CameraCaptureActivity extends BaseColorActivity implements Callback, OnClickListener {

	private static final int SCAN_REQUEST_CODE = 100;
	private static final int CAMERA_PERMISSION = 110;

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private boolean hasFlashLight = false;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private Camera camera = null;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Toolbar toolbar;

	@Override
	protected int getLayoutRsId() {
		return R.layout.camera_capture_activity;
	}

	/**
	 * 加载界面
	 */
	@Override
	protected void setUI() {
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		TextView mButtonBack = (TextView) findViewById(R.id.button_back);
		toolbar = findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);

		ToggleButton mLight = findViewById(R.id.btn_toggle);
		TextView mAlbum = findViewById(R.id.btn_Album);
		mAlbum.setOnClickListener(this);
		mButtonBack.setOnClickListener(this);
		mLight.setOnClickListener(this);

		PackageManager pm = this.getPackageManager();
		hasFlashLight = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}


	/**
	 * 初始化变量
	 */
	@Override
	protected void initData() {
		CameraManager.init(getApplication());

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		getViewfinderView().setOnFlashLightStateChangeListener(new ViewfinderView.onFlashLightStateChangeListener() {
			@Override
			public void openFlashLight(boolean open) {
				turnOnFlashLight(open);
				getViewfinderView().reOnDraw();//重新刷新页面
			}
		});

		playBeep = true;
		//getRingerMode() ——返回当前的铃声模式。如RINGER_MODE_NORMAL（普通）、
		// RINGER_MODE_SILENT（静音）、RINGER_MODE_VIBRATE（震动）
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}


	protected void turnOnFlashLight(boolean open) {
		// 带闪光灯
		if (hasFlashLight) {
			if (!open) {
				if (camera == null) {
					camera = CameraManager.getCamera();
				}
				Camera.Parameters parameters = camera.getParameters();
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(parameters);
				// tvFlash.setText(R.string.flash);
			} else {
				try {
					if (camera == null) {
						camera = CameraManager.getCamera();
					}
					Camera.Parameters parameters = camera.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					camera.setParameters(parameters);
					// tvFlash.setText(R.string.flash);
				} catch (Exception e) {
					CustomToast.showToast(this,"您的设备不支持闪光灯");
				}
			}
		} else {
			CustomToast.showToast(this,"您的设备不支持闪光灯");
		}
	}

	/**
	 * 
	 * 二维码识别解码
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, final Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			CustomToast.showToast(CameraCaptureActivity.this, "Scan failed!");
		} else {
			// 把扫到的二维码保存到本地
        	String savePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/";
			File outPath = new File(savePath);
			if (!outPath.exists()) {
				outPath.mkdirs();
			}

			String imgName = savePath + "Capture.jpg";
			try {
		        File file = new File(imgName);
		        FileOutputStream out = new FileOutputStream(file);
		        barcode.compress(Bitmap.CompressFormat.JPEG, 100, out);
		        out.flush();
		        out.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		        CustomToast.showToast(CameraCaptureActivity.this, e.getMessage());
		    }
	        
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("barcode", resultString);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);
		}
		CameraCaptureActivity.this.finish();
	}

	/**
	 * 相机初始化
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			//AssetFileDescriptor afd = getAssets().openFd("");

			//AssetFileDescriptor 读取(android)app的raw文件夹下的数据
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			//mp是MediaPlayer实例对象,fd是读取raw文件夹下的文件信息类
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);//设置play后是为了防止失效
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);//回滚起点
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.button_back) {

			CameraCaptureActivity.this.finish();

		}else if(arg0.getId() == R.id.btn_toggle) {


		}else if(arg0.getId() == R.id.btn_Album ) {
			Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
			// 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
			intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
			startActivityForResult(intentToPickPic, SCAN_REQUEST_CODE);
		}


	}
}