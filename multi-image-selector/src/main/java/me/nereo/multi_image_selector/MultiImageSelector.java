package me.nereo.multi_image_selector;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 图片选择器
 * Created by nereo on 16/3/17.
 */
public class MultiImageSelector {

	private LinkedHashSet<String> mChooseValue;
	private Context context;

	{
		mChooseValue = new LinkedHashSet<>();
	}

	private MultiImageControl multiImageControl;

	private MultiImageControl.MultiImageResult multiImageResult = new MultiImageControl.MultiImageResult() {
		@Override
		public void multiImageReslut(Collection<String> result) {

//			mChooseValue = (LinkedHashSet<String>) ((LinkedHashSet) result).clone();
//			for (String s : result) {
//				Log.i("Tag", "000000  " + s);
//			}

			mChooseValue.addAll(result);
			commit(context);
			multiImageControl.dis();
		}
	};

	public MultiImageSelector(Context context) {
		multiImageControl = MultiImageControl.getSingleton();
		this.context = context;
	}


	public MultiImageSelector showCamera(boolean show) {
		multiImageControl.showCamera(show);
		return this;
	}

	public MultiImageSelector count(int count) {
		multiImageControl.count(count);
		return this;
	}

	public MultiImageSelector origin(ArrayList<String> images) {

		for (String s : images) {
			Log.i("Tag", "origin  " + s);
		}
		multiImageControl.origin(images);
		return this;
	}

	public void start(Context context, MultiImageCallBack multiImageCallBack) {
		multiImageControl.start(context, multiImageResult);
		this.multiImageCallBack = multiImageCallBack;
	}


	protected void commit(Context context) {
		if (multiImageCallBack != null) {
			multiImageCallBack.multiSelectorImages(mChooseValue);
		}
		Intent intent = new Intent();
		intent.setClassName(context, this.context.getClass().getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


	MultiImageCallBack multiImageCallBack;

	/**
	 * 外部请求返回
	 */
	public interface MultiImageCallBack {
		void multiSelectorImages(Collection<String> result);
	}


}
