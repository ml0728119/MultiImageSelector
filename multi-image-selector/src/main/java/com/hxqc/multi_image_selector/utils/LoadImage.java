package com.hxqc.multi_image_selector.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.hxqc.multi_image_selector.R;

/**
 * Created 胡俊杰
 * 2018/7/17.
 * Todo:
 */
public class LoadImage {
	public static void loadImage(Context context, ImageView imageView,String path){
		RequestOptions options = new RequestOptions()
				.centerCrop()
				.placeholder(R.drawable.mis_default_error)
				.error(R.drawable.mis_default_error)
				.priority(Priority.HIGH);
		Glide.with(context)
				.load(path)
				.apply(options)
				.into(imageView);
	}
}
