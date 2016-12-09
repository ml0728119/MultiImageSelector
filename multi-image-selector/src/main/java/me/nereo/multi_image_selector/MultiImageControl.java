package me.nereo.multi_image_selector;

import android.Manifest;
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
	// Single choice
	public static final int MODE_SINGLE = 0;
	// Multi choice
	public static final int MODE_MULTI = 1;

	public static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;

	private boolean mShowCamera = true;
	private int mMaxCount = 1;
	private int mMode = MODE_SINGLE;
	private static MultiImageControl sSelector;
	private LinkedHashSet<String> mChooseValue;
	private Context context;

	{
		mChooseValue = new LinkedHashSet<>(9);
	}


	private MultiImageControl() {
	}


	public static MultiImageControl getSingleton() {
		if (sSelector == null) {
			sSelector = new MultiImageControl();
		}
		return sSelector;
	}

	public LinkedHashSet<String> getChooseValue() {
		return mChooseValue;
	}

	public MultiImageControl showCamera(boolean show) {
		mShowCamera = show;
		return sSelector;
	}

	public MultiImageControl count(int count) {
		mMaxCount = count;
		if (mMaxCount <= 1) {
			mMode = MODE_SINGLE;
			mMaxCount = 1;
		} else {
			mMode = MODE_MULTI;
		}
		return sSelector;
	}

	public MultiImageControl origin(ArrayList<String> images) {
		if (images != null) {
			mChooseValue.addAll(images);
		}
		return sSelector;
	}

	public void start(Context context, MultiImageResult multiImageCallBack) {
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
	 * @return  增加返回true  未增加返回false
	 */
	protected boolean addResultImage(Context context, String value) {

		if (mMode == MODE_SINGLE) {
			mChooseValue.clear();
		}
		if (mMaxCount <= mChooseValue.size()) {
			Toast.makeText(context, context.getString(R.string.mis_max_count,mMaxCount), Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mChooseValue.add(value);
			return true;
		}
	}

	/**
	 *
	 * @param value
	 */
	protected void removeResultImage(String value) {
		mChooseValue.remove(value);
	}

	protected void commit(Context context) {
		if (multiImageResult != null) {
			multiImageResult.multiImageReslut(mChooseValue);
		}
	}


	MultiImageResult multiImageResult;

	//内部调用
	protected interface MultiImageResult {
		void multiImageReslut(Collection<String> result);
	}

	public int getMode() {
		return mMode;
	}

	public int getMaxCount() {
		return mMaxCount;
	}

	public void dis() {
		mChooseValue.clear();
		sSelector = null;
	}
}
