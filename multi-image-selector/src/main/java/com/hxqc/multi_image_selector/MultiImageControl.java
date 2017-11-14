package com.hxqc.multi_image_selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 图片选择器
 * Created by nereo on 16/3/17.
 */
public class MultiImageControl {

	private LinkedHashSet<String> mChooseValue;

	{
		mChooseValue = new LinkedHashSet<>(9);
	}

	// Single choice
	public static final int MODE_SINGLE = 0;
	// Multi choice
	public static final int MODE_MULTI = 1;
	protected int mMode = MODE_SINGLE;
	protected int mMaxCount = 1;

	private static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;
	private boolean mShowCamera = true;
	private static MultiImageControl mControl;

	private boolean crop = false;
	private MultiImageResult multiImageResult;
	private boolean onlyCamera = false;//只有相机
	private float ratioX = 16, ratioY = 9;

	//内部调用
	interface MultiImageResult {
		void multiImageResult(Collection<String> result);

		void onCancelResult();
	}


	private MultiImageControl() {
	}


	public static MultiImageControl getSingleton() {
		if (mControl == null) {
			mControl = new MultiImageControl();
		}
		return mControl;
	}

	public synchronized void destroy() {
		mControl = null;
	}

	public LinkedHashSet<String> getChooseValue() {
		return mChooseValue;
	}


	public MultiImageControl onlyCamera(boolean onlyCamera) {
		this.onlyCamera = onlyCamera;
		return mControl;
	}

	public MultiImageControl showCamera(boolean show) {
		mShowCamera = show;
		return mControl;
	}

	public MultiImageControl count(int count) {
		mMaxCount = count;
		if (mMaxCount <= 1) {
			mMode = MODE_SINGLE;
			mMaxCount = 1;
		} else {
			mMode = MODE_MULTI;
		}
		return mControl;
	}

	public MultiImageControl origin(ArrayList<String> images) {
		if (images != null) {
			mChooseValue.addAll(images);
		}
		return mControl;
	}


	/**
	 * 是否对图片进行裁切  仅对单选图片有效
	 */
	public MultiImageControl cropPhoto(boolean crop) {
		if (mMode == MODE_SINGLE) {
			this.crop = crop;
		}
		return mControl;
	}


	public MultiImageControl cropWithAspectRatio(float x, float y) {
		ratioX = x;
		ratioY = y;
		return this;
	}

	public float getRatioX() {
		return ratioX;
	}

	public float getRatioY() {
		return ratioY;
	}

	public MultiImageControl start(Context context, MultiImageResult multiImageCallBack) {
		this.multiImageResult = multiImageCallBack;
		if (onlyCamera && mShowCamera) {
			toCameraActivity(context);
		} else {
			toMultiImageSelectorActivity(context);
		}
		return mControl;
	}


	/**
	 * @return 增加返回true  未增加返回false
	 */
	public boolean addResultImage(Context context, String value) {

		if (mMode == MODE_SINGLE) {
			mChooseValue.clear();

		}
		if (mMaxCount <= mChooseValue.size()) {
			Toast.makeText(context, context.getString(R.string.mis_max_count, mMaxCount), Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mChooseValue.add(value);
			return true;
		}
	}

	public void removeResultImage(String value) {
		mChooseValue.remove(value);
	}

	public void commit(Activity context) {
		if (crop && (mMode == MODE_SINGLE)) {
			String[] list = new String[1];
			mChooseValue.toArray(list);
			toCrop(list[0], context);

		} else {
			toFinish();
		}
	}

	/**
	 * 点击返回键  直接取消选图
	 */
	public void cancel() {
		if (multiImageResult != null) {
			multiImageResult.onCancelResult();
		}
		dis();
	}

	/**
	 * 结束
	 */
	public synchronized void toFinish() {
		if (multiImageResult != null) {
			multiImageResult.multiImageResult(mChooseValue);
		}
		dis();
	}

	public int getMode() {
		return mMode;
	}

	public int getMaxCount() {
		return mMaxCount;
	}


	public void dis() {
		mChooseValue.clear();
		mControl = null;
	}

	/**
	 * 去选择页
	 */
	public void toMultiImageSelectorActivity(Context context) {
		Intent intent = new Intent(context, MultiImageSelectorActivity.class);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
		context.startActivity(intent);
	}

	/**
	 * 去相机
	 */
	public static void toCameraActivity(Context context) {
		Intent intent = new Intent(context, OnlyCameraPermissionActivity.class);
		context.startActivity(intent);
	}

	/**
	 * @param fromCropPath 文件的绝对路径
	 */
	public void toCrop(String fromCropPath, Activity context) {
		Intent intent = new Intent(context, CropResultActivity.class);
		intent.putExtra("fromPath", fromCropPath);
		context.startActivity(intent);
	}


}
