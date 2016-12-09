package me.nereo.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
	private int mMode = MODE_SINGLE;
	private int mMaxCount = 1;

	public static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;
	private boolean mShowCamera = true;

	private static MultiImageControl mControl;

	private Context context;
	private boolean crop = false;
	private MultiImageResult multiImageResult;

	//内部调用
	interface MultiImageResult {
		void multiImageResult(Collection<String> result);
	}


	private MultiImageControl() {
	}


	public static MultiImageControl getSingleton() {
		if (mControl == null) {
			mControl = new MultiImageControl();
		}
		return mControl;
	}

	public LinkedHashSet<String> getChooseValue() {
		return mChooseValue;
	}

	MultiImageControl showCamera(boolean show) {
		mShowCamera = show;
		return mControl;
	}

	MultiImageControl count(int count) {
		mMaxCount = count;
		if (mMaxCount <= 1) {
			mMode = MODE_SINGLE;
			mMaxCount = 1;
		} else {
			mMode = MODE_MULTI;
		}
		return mControl;
	}

	MultiImageControl origin(ArrayList<String> images) {
		if (images != null) {
			mChooseValue.addAll(images);
		}
		return mControl;
	}


	/**
	 * 是否对图片进行裁切  仅对单选图片有效
	 */
	MultiImageControl cropPhoto(boolean crop) {
		if (mMode == MODE_SINGLE) {
			this.crop = crop;
		}
		return mControl;
	}

	void start(Context context, MultiImageResult multiImageCallBack) {
		if (hasPermission(context)) {
			this.multiImageResult = multiImageCallBack;
			context.startActivity(createIntent(context));
		} else {
			Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
		}
	}


	private boolean hasPermission(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// Permission was added in API Level 16
			return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED;
		}
		return true;
	}

	private Intent createIntent(Context context) {
		this.context = context;
		Intent intent = new Intent(context, MultiImageSelectorActivity.class);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
		return intent;
	}


	//--------------------------------------

	/**
	 * @return 增加返回true  未增加返回false
	 */
	boolean addResultImage(Context context, String value) {

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

	void removeResultImage(String value) {
		mChooseValue.remove(value);
	}

	protected void commit(Activity context) {
		if (crop && mMode == MODE_SINGLE) {
			String[] list = new String[1];
			mChooseValue.toArray(list);
			toCrop(list[0],context);
		} else {
			toFinish();
		}
	}

	/**
	 * 结束
	 */
	public void toFinish() {
		if (multiImageResult != null) {
			multiImageResult.multiImageResult(mChooseValue);
		}
	}


	public int getMode() {
		return mMode;
	}

	public int getMaxCount() {
		return mMaxCount;
	}

	void dis() {
		mChooseValue.clear();
		mControl = null;
	}

	/**
	 *
	 * @param fromCropPath  文件的绝对路径
	 */
	void toCrop(String fromCropPath,Activity context) {
		Intent intent = new Intent(context, CropResultActivity.class);
		intent.putExtra("fromPath", fromCropPath);
		context.startActivity(intent);
	}
}
