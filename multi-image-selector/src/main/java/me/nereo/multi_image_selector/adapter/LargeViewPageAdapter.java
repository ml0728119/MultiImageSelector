package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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


	public LargeViewPageAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void setDatas(ArrayList<Image> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public LargeViewPageAdapter(Context context, ArrayList<Image> datas) {
		this.mContext = context;
		this.datas = datas;
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
//		Log.i("Tag", "ss   " + datas.get(position).path);

		Glide.with(mContext).load("file://" + datas.get(position).path).into(photoView);

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
