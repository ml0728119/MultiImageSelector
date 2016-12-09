package me.nereo.multi_image_selector;

import android.content.Context;
import android.content.Intent;

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
		public void multiImageResult(Collection<String> result) {
			mChooseValue.addAll(result);
			commit(context);
			multiImageControl.dis();
		}
	};

	public MultiImageSelector(Context context) {
		multiImageControl = MultiImageControl.getSingleton();
		this.context = context;
	}

	/**
	 * 是否包含相机 默认包含
	 *
	 * @param show
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
	public MultiImageSelector cropPhoto(boolean crop) {
		multiImageControl.cropPhoto(crop);
		return this;
	}

	public MultiImageSelector cropWithAspectRatio(float x, float y ){
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

	private MultiImageCallBack multiImageCallBack;

	/**
	 * 外部请求返回
	 */
	public interface MultiImageCallBack {
		void multiSelectorImages(Collection<String> result);
	}



}
