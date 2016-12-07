package me.nereo.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 图片选择器
 * Created by nereo on 16/3/17.
 */
public class MultiImageSelector2 {

	public static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;

	private boolean mShowCamera = true;
	private int mMaxCount = 9;
	private int mMode = MultiImageSelectorActivity.MODE_MULTI;
	private ArrayList<String> mOriginData;
	private static MultiImageSelector2 sSelector;
	Set<String> mChooseValue;

	{
		mChooseValue = new LinkedHashSet<>(9);
	}


	private MultiImageSelector2() {
	}


	public static MultiImageSelector2 getSingleton() {
		if (sSelector == null) {
			sSelector = new MultiImageSelector2();
		}
		return sSelector;
	}

	public MultiImageSelector2 showCamera(boolean show) {
		mShowCamera = show;
		return sSelector;
	}

	public MultiImageSelector2 count(int count) {
		mMaxCount = count;
		if(mMaxCount==1){
			mMode = MultiImageSelectorActivity.MODE_SINGLE;
		}else {
			mMode = MultiImageSelectorActivity.MODE_MULTI;
		}
		return sSelector;
	}
	public MultiImageSelector2 origin(ArrayList<String> images) {
		mOriginData = images;
//		mChooseValue.addAll(images);
		return sSelector;
	}

	@Deprecated
	public MultiImageSelector2 single() {
		mMode = MultiImageSelectorActivity.MODE_SINGLE;
		return sSelector;
	}
	@Deprecated
	public MultiImageSelector2 multi() {
		mMode = MultiImageSelectorActivity.MODE_MULTI;
		return sSelector;
	}



	public void start(Activity activity, int requestCode) {
		final Context context = activity;
		if (hasPermission(context)) {
			activity.startActivityForResult(createIntent(context), requestCode);
		} else {
			Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
		}
	}

	public void start(Fragment fragment, int requestCode) {
		final Context context = fragment.getContext();
		if (hasPermission(context)) {
			fragment.startActivityForResult(createIntent(context), requestCode);
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
		Intent intent = new Intent(context, MultiImageSelectorActivity.class);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);

		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);

		Log.i("Tag",context.getClass().getName());
		return intent;
	}


	//--------------------------------------






	public void addResultImage(String value){
		mChooseValue.add(value);
	}
	public void removeResultImage(String value){
		mChooseValue.remove(value);
	}



}
