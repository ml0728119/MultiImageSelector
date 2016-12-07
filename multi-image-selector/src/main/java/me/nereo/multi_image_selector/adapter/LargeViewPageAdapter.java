package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;
import uk.co.senab.photoview.PhotoView;

/**
 * Created 胡俊杰
 * 2016/12/7.
 * Todo:
 */

public class LargeViewPageAdapter extends PagerAdapter {
	Context mContext;
	ArrayList<Image> datas;
	int mScreenWidth, mScreenHeight;

	public LargeViewPageAdapter(Context context, ArrayList<Image> datas) {
		this.mContext = context;
		this.datas = datas;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		PhotoView photoView = new PhotoView(container.getContext());
		photoView.setBackgroundColor(Color.BLACK);
		container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		Log.i("Tag", "ss   " + datas.get(position).path);
		Picasso.with(mContext)
				.load("file://" + datas.get(position).path)
//				.resize(mScreenWidth, mScreenHeight)
//				.fit()
				.config(Bitmap.Config.RGB_565)
				.placeholder(R.drawable.mis_default_error)

				.into(photoView, new Callback() {
					@Override
					public void onSuccess() {
						Log.i("Tag","11111111");
					}

					@Override
					public void onError() {
						Log.i("Tag","onError   ");
					}
				});
		return photoView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}
