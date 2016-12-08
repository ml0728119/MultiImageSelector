package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageControl;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class ImageGridAdapter extends BaseAdapter {

	private static final int TYPE_CAMERA = 0;
	private static final int TYPE_NORMAL = 1;

	private Context mContext;

	private LayoutInflater mInflater;
	private boolean showCamera = true;
	private boolean showSelectIndicator = true;

	private ArrayList<Image> mImages = new ArrayList<>();
//	private List<Image> mSelectedImages = new ArrayList<>();

	final int mGridWidth;

	public ImageGridAdapter(Context context, boolean showCamera, int column) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showCamera = showCamera;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			wm.getDefaultDisplay().getSize(size);
			width = size.x;
		} else {
			width = wm.getDefaultDisplay().getWidth();
		}
		mGridWidth = width / column;
	}

	/**
	 * 显示选择指示器
	 *
	 * @param b
	 */
	public void showSelectIndicator(boolean b) {
		showSelectIndicator = b;
	}

	public void setShowCamera(boolean b) {
		if (showCamera == b) return;
		showCamera = b;
		notifyDataSetChanged();
	}

	public boolean isShowCamera() {
		return showCamera;
	}

//	/**
//	 * 通过图片路径设置默认选择
//	 *
//	 * @param resultList
//	 */
//	public void setDefaultSelected(LinkedHashSet<String> resultList) {
//		for (String path : resultList) {
//			Image image = getImageByPath(path);
//			if (image != null) {
//				mSelectedImages.add(image);
//			}
//		}
//		if (mSelectedImages.size() > 0) {
//			notifyDataSetChanged();
//		}
//	}

//	private Image getImageByPath(String path) {
//		if (mImages != null && mImages.size() > 0) {
//			for (Image image : mImages) {
//				if (image.path.equalsIgnoreCase(path)) {
//					return image;
//				}
//			}
//		}
//		return null;
//	}

	/**
	 * 设置数据集
	 *
	 * @param images
	 */
	public void setData(List<Image> images) {
		if (images != null && images.size() > 0) {
			mImages = (ArrayList<Image>) images;
		} else {
			mImages.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (showCamera) {
			return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
		}
		return TYPE_NORMAL;
	}

	@Override
	public int getCount() {
		return showCamera ? mImages.size() + 1 : mImages.size();
	}

	@Override
	public Image getItem(int i) {
		if (showCamera) {
			if (i == 0) {
				return null;
			}
			return mImages.get(i - 1);
		} else {
			return mImages.get(i);
		}
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		if (isShowCamera()) {
			if (i == 0) {
				view = mInflater.inflate(R.layout.mis_list_item_camera, viewGroup, false);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onImageSelectorListener != null)
							onImageSelectorListener.onCamera();
					}
				});
				return view;
			}
		}

		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.mis_list_item_image, viewGroup, false);
			holder = new ViewHolder(view);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (holder != null) {
			holder.bindData(getItem(i), i);
		}

		return view;
	}

	class ViewHolder {
		ImageView image;
		CheckBox indicator;
		View mask;
		View rootView;

		ViewHolder(View rootView) {
			image = (ImageView) rootView.findViewById(R.id.image);
			indicator = (CheckBox) rootView.findViewById(R.id.checkmark);
			mask = rootView.findViewById(R.id.mask);
			rootView.setTag(this);
			this.rootView = rootView;

		}

		void bindData(final Image data, final int position) {
			if (data == null) return;
			// 处理单选和多选状态
			if (showSelectIndicator) {
				indicator.setVisibility(View.VISIBLE);
				if (MultiImageControl.getSingleton().getChooseValue().contains(data.path)) {
					// 设置选中状态
					indicator.setChecked(true);
					mask.setVisibility(View.VISIBLE);
				} else {
					// 未选择
					indicator.setChecked(false);
					mask.setVisibility(View.GONE);
				}
			} else {
				indicator.setVisibility(View.GONE);
			}
			File imageFile = new File(data.path);
			if (imageFile.exists()) {
				Glide.with(mContext)
						.load(data.path)
						.centerCrop()
						.placeholder(R.drawable.mis_default_error)
						.crossFade()
						.into(image);

			} else {
				image.setImageResource(R.drawable.mis_default_error);
			}

			indicator.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onImageSelectorListener != null) {
						onImageSelectorListener.onCheck(position, data, indicator.isChecked());
					}
					if (indicator.isChecked()) {
						mask.setVisibility(View.VISIBLE);
					} else {
						mask.setVisibility(View.GONE);
					}
				}
			});
			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onImageSelectorListener != null) {
						onImageSelectorListener.onItemClick(position, data, mImages);
					}
				}
			});
		}
	}

	OnImageSelectorListener onImageSelectorListener;

	public interface OnImageSelectorListener {
		void onCheck(int position, Image image, boolean isCheck);

		void onItemClick(int position, Image image, ArrayList<Image> data);

		void onCamera();
	}

	public void setOnImageSelectorListener(OnImageSelectorListener onImageSelectorListener) {
		this.onImageSelectorListener = onImageSelectorListener;
	}
}
