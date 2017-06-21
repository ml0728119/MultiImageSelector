package me.nereo.multi_image_selector;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 说明:相机
 *
 * @author: 吕飞
 * @since: 2016-10-31
 * Copyright:恒信汽车电子商务有限公司
 */
public class MisCameraActivity extends Activity implements OnClickListener {
	private static final String TAG = "MisCameraActivity";
	TextView mCancelView;
	ImageView mFlashView;
	ImageView mTakePhotoView;
	ImageView mPreviewView;        //预览
	TextView mCommitView;       //提交
	TextView mRepetitionView;  //重拍

	RelativeLayout mPreviewLayout;
	RelativeLayout mCameraLayout;
	private CameraView mCameraView;
	public static final String PHOTO_PATH = "photo_path";
	private static final int[] FLASH_OPTIONS = {
			CameraView.FLASH_AUTO,
			CameraView.FLASH_OFF,
			CameraView.FLASH_ON,
	};

	private static final int[] FLASH_ICONS = {
			R.drawable.ic_flash_auto,
			R.drawable.ic_flash_off,
			R.drawable.ic_flash_on,
	};

	private Handler mBackgroundHandler;
	private int mCurrentFlash;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置全屏无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		setContentView(R.layout.activity_mis_camera);
		initView();
	}

	protected void initView() {
		mPreviewLayout = (RelativeLayout)findViewById(R.id.mis_preview_layout);
		mCameraLayout = (RelativeLayout) findViewById(R.id.mis_camera_layout);
		mCameraView = (CameraView) findViewById(R.id.mis_camera);
		if (mCameraView != null) {
			mCameraView.addCallback(mCallback);
		}
		mFlashView = (ImageView) findViewById(R.id.mis_flash);
		mCancelView = (TextView) findViewById(R.id.mis_cancel);

		mTakePhotoView = (ImageView) findViewById(R.id.mis_take_photo);
		mTakePhotoView.setOnClickListener(this);
		mCancelView.setOnClickListener(this);
		mFlashView.setOnClickListener(this);

		mPreviewView = (ImageView) findViewById(R.id.mis_preview);
		mCommitView = (TextView) findViewById(R.id.mis_commit);
		mRepetitionView = (TextView) findViewById(R.id.mis_repetition);
	}

	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.mis_take_photo) {
			if (mCameraView != null) {
				mCameraView.takePicture();
			}
		} else if (i == R.id.mis_cancel) {
			finish();
		} else if (i == R.id.mis_flash) {
			if (mCameraView != null) {
				mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
				mFlashView.setBackgroundResource(FLASH_ICONS[mCurrentFlash]);
				mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCameraView.start();
	}

	@Override
	protected void onPause() {
		mCameraView.stop();
		super.onPause();
	}

	private CameraView.Callback mCallback
			= new CameraView.Callback() {

		@Override
		public void onCameraOpened(CameraView cameraView) {
//			Log.d(TAG, "onCameraOpened");
		}

		@Override
		public void onCameraClosed(CameraView cameraView) {
//			Log.d(TAG, "onCameraClosed");
		}

		@Override
		public void onPictureTaken(CameraView cameraView, final byte[] data) {
//			Log.d(TAG, "onPictureTaken " + data.length);
			showPreview(data);
		}
	};
	String saveFilePath;

	private void saveFile(final byte[] data) {
		getBackgroundHandler().post(new Runnable() {
			@Override
			public void run() {

				File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						System.currentTimeMillis() + ".jpg");
				saveFilePath = file.getPath();
//				Log.d(TAG, "onPictureTaken " + file.getPath());
				OutputStream os = null;
				try {
					os = new FileOutputStream(file);
					os.write(data);
					os.close();
				} catch (IOException e) {
//					Log.w(TAG, "Cannot write to " + file, e);
				} finally {
					if (os != null) {
						try {
							os.close();
							MultiImageControl.getSingleton().addResultImage(MisCameraActivity.this, saveFilePath);
							MultiImageControl.getSingleton().commit(MisCameraActivity.this);
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
		});

	}

	private void showPreview(final byte[] data) {
		mPreviewLayout.setVisibility(View.VISIBLE);
		mCameraLayout.setVisibility(View.GONE);

		mCommitView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveFile(data);
			}
		});
		mRepetitionView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPreviewLayout.setVisibility(View.GONE);
				mCameraLayout.setVisibility(View.VISIBLE);
			}
		});
		Glide.with(this).load(data).into(mPreviewView);
	}

	private Handler getBackgroundHandler() {
		if (mBackgroundHandler == null) {
			HandlerThread thread = new HandlerThread("background");
			thread.start();
			mBackgroundHandler = new Handler(thread.getLooper());
		}
		return mBackgroundHandler;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBackgroundHandler != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				mBackgroundHandler.getLooper().quitSafely();
			} else {
				mBackgroundHandler.getLooper().quit();
			}
			mBackgroundHandler = null;
		}
	}


}
