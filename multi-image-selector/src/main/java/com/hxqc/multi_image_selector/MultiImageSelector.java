package com.hxqc.multi_image_selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 图片选择器
 * Created by nereo on 16/3/17.
 */
public class MultiImageSelector {

	private Context context;

	public static MultiImageControl multiImageControl;

	private MultiCancelListener multiCancelListener;

	private MultiImageControl.MultiImageResult multiImageResult = new MultiImageControl.MultiImageResult() {
		@Override
		public void multiImageResult(Collection<String> result) {
			commit(context);

		}

		@Override
		public void onCancelResult() {
			cancel();
		}
	};

	public MultiImageSelector(Context context) {
		multiImageControl = new MultiImageControl();

		this.context = context;
	}

	/**
	 * 只有相机
	 */
	public MultiImageSelector onlyCamera() {
		multiImageControl.onlyCamera(true);
		return this;
	}

	public MultiImageSelector onlyCamera(boolean onlyCamera) {
		multiImageControl.onlyCamera(onlyCamera);
		return this;
	}

	/**
	 * 是否包含相机 默认包含
	 */
	public MultiImageSelector showCamera(boolean show) {
		multiImageControl.showCamera(show);
		return this;
	}

	/**
	 * 选择图片个数  默认1
	 */
	public MultiImageSelector count(int count) {
		multiImageControl.count(count);
		return this;
	}

	/**
	 * 已选图片路径地址
	 */
	public MultiImageSelector origin(ArrayList<String> images) {
		multiImageControl.origin(images);
		return this;
	}

	/**
	 * 是否对图片进行裁切  仅对单选图片有效
	 */
	public MultiImageSelector cropPhoto(boolean acrop) {
		multiImageControl.cropPhoto(acrop);
		multiImageControl.crop = true;
		return this;
	}

	public MultiImageSelector coverView(int layoutID) {
		multiImageControl.coverView(layoutID);
		return this;
	}

	public MultiImageSelector coverView(View coverView) {
		multiImageControl.coverView(coverView);

		return this;
	}

	/**
	 * 比例裁切
	 */
	public MultiImageSelector cropWithAspectRatio(float x, float y) {
		multiImageControl.cropWithAspectRatio(x, y);
		return this;
	}


	public MultiImageSelector start(Context context, MultiImageCallBack multiImageCallBack) {
		multiImageControl.start(context, multiImageResult);
		this.multiImageCallBack = multiImageCallBack;
		return this;
	}


	/**
	 * 点击返回键 回调
	 */
	public interface MultiCancelListener {
		void onCancel();
	}

	/**
	 * 点击返回键 回调
	 */
	public MultiImageSelector setOnCancelListener(MultiCancelListener multiCancelListener) {
		this.multiCancelListener = multiCancelListener;
		return this;
	}

	public void cancel() {
		if (multiCancelListener != null) {
			multiCancelListener.onCancel();
		}
	}

	private void commit(Context context) {
		if (multiImageCallBack != null) {
			multiImageCallBack.multiSelectorImages(multiImageControl.mChooseValue);
		}
		Intent intent = new Intent();
		intent.setClassName(context, this.context.getClass().getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);

	}

	private MultiImageCallBack multiImageCallBack;

	/**
	 * 外部请求返回
	 */
	public interface MultiImageCallBack {
		void multiSelectorImages(Collection<String> result);
	}


	static public class MultiImageControl {

		private LinkedHashSet<String> mChooseValue;

		{
			mChooseValue = new LinkedHashSet<>(9);
		}

		// Single choice
		static final int MODE_SINGLE = 0;
		// Multi choice
		static final int MODE_MULTI = 1;

		int mMode = MODE_SINGLE;
		int mMaxCount = 1;
		boolean mShowCamera = true;
		boolean crop = false;
		MultiImageResult multiImageResult;
		boolean onlyCamera = false;//只有相机
		float ratioX = 16, ratioY = 9;
		protected View mCoverView = null;
		protected int mCoverLayoutID = 0;

		//内部调用
		interface MultiImageResult {
			void multiImageResult(Collection<String> result);

			void onCancelResult();
		}


		private MultiImageControl() {
		}


		public LinkedHashSet<String> getChooseValue() {
			return mChooseValue;
		}


		MultiImageControl onlyCamera(boolean onlyCamera) {
			this.onlyCamera = onlyCamera;
			return this;
		}

		MultiImageControl showCamera(boolean show) {
			mShowCamera = show;
			return this;
		}

		MultiImageControl count(int count) {
			mMaxCount = count;
			if (mMaxCount <= 1) {
				mMode = MODE_SINGLE;
				mMaxCount = 1;
			} else {
				mMode = MODE_MULTI;
			}
			return this;
		}

		MultiImageControl origin(ArrayList<String> images) {
			if (images != null) {
				mChooseValue.addAll(images);
			}
			return this;
		}


		/**
		 * 是否对图片进行裁切  仅对单选图片有效
		 */
		MultiImageControl cropPhoto(boolean crop) {
			if (mMode == MODE_SINGLE) {
				this.crop = crop;
			}
			return this;
		}


		MultiImageControl cropWithAspectRatio(float x, float y) {
			ratioX = x;
			ratioY = y;
			return this;
		}


		MultiImageControl coverView(int layoutID) {
			this.mCoverLayoutID = layoutID;
			return this;
		}

		MultiImageControl coverView(View coverView) {
			this.mCoverView = coverView;
			return this;
		}

		float getRatioX() {
			return ratioX;
		}

		float getRatioY() {
			return ratioY;
		}

		MultiImageControl start(Context context, MultiImageResult multiImageCallBack) {
			this.multiImageResult = multiImageCallBack;
			if (onlyCamera && mShowCamera) {
				toCameraActivity(context);
			} else {
				toMultiImageSelectorActivity(context);
			}
			return this;
		}


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
		void cancel() {
			if (multiImageResult != null) {
				multiImageResult.onCancelResult();
			}
			dis();
		}

		/**
		 * 结束
		 */
		synchronized void toFinish() {
			if (multiImageResult != null) {
				multiImageResult.multiImageResult(mChooseValue);
			}
			dis();
		}

		int getMode() {
			return mMode;
		}

		int getMaxCount() {
			return mMaxCount;
		}


		void dis() {
			mCoverView = null;
			mCoverLayoutID = 0;
			mChooseValue.clear();
		}

		/**
		 * 去选择页
		 */
		protected void toMultiImageSelectorActivity(Context context) {
			Intent intent = new Intent(context, MultiImageSelectorActivity.class);
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
			context.startActivity(intent);
		}

		/**
		 * 去相机
		 */
		protected static void toCameraActivity(Context context) {
			Intent intent = new Intent(context, OnlyCameraPermissionActivity.class);
			context.startActivity(intent);
		}

		/**
		 * @param fromCropPath 文件的绝对路径
		 */
		protected void toCrop(String fromCropPath, Activity context) {
			Intent intent = new Intent(context, CropResultActivity.class);
			intent.putExtra("fromPath", fromCropPath);
			context.startActivity(intent);
		}


	}


}
