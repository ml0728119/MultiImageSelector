package me.nereo.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.adapter.FolderAdapter;
import me.nereo.multi_image_selector.adapter.ImageGridAdapter;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.ScreenUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static me.nereo.multi_image_selector.LoadControl.LOADER_ALL;

/**
 * Multi image selector Fragment
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/5/18.
 */
public class MultiImageSelectorFragment extends Fragment implements ImageGridAdapter.OnImageSelectorListener, LoadControl.OnLoadFinishListener {

	public static final String TAG = "MultiImageSelectorFragment";

	private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
	private static final int REQUEST_CAMERA = 100;

	private static final String KEY_TEMP_FILE = "key_temp_file";

	// Single choice
	public static final int MODE_SINGLE = 0;
	// Multi choice
	public static final int MODE_MULTI = 1;

	/**
	 * Max image size，int，
	 */
	public static final String EXTRA_SELECT_COUNT = "max_select_count";
	/**
	 * Select mode，{@link #MODE_MULTI} by default
	 */
	public static final String EXTRA_SELECT_MODE = "select_count_mode";
	/**
	 * Whether show camera，true by default
	 */
	public static final String EXTRA_SHOW_CAMERA = "show_camera";

	private GridView mGridView;
	private Callback mCallback;

	private ImageGridAdapter mImageAdapter;
	private FolderAdapter mFolderAdapter;

	private ListPopupWindow mFolderPopupWindow;
	private TextView mCategoryText;
	private View mPopupAnchorView;
	private File mTmpFile;

	LoadControl mLoaderControl;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mCallback = (Callback) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mis_fragment_multi_image, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

//		final int mode = selectMode();
//		if (mode == MODE_MULTI) {
//			resultList = MultiImageControl.getSingleton().getChooseValue();
//		}
		mLoaderControl = new LoadControl(getContext());
		mLoaderControl.setOnLoadFinishListener(this);

		mImageAdapter = new ImageGridAdapter(getActivity(), showCamera(), 3);
		mFolderAdapter = new FolderAdapter(getActivity());

		mPopupAnchorView = view.findViewById(R.id.footer);
		mCategoryText = (TextView) view.findViewById(R.id.category_btn);
		mCategoryText.setText(R.string.mis_folder_all);
		mCategoryText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (mFolderPopupWindow == null) {
					createPopupFolderList();
				}

				if (mFolderPopupWindow.isShowing()) {
					mFolderPopupWindow.dismiss();
				} else {
					mFolderPopupWindow.show();
					int index = mFolderAdapter.getSelectIndex();
					index = index == 0 ? index : index - 1;
					mFolderPopupWindow.getListView().setSelection(index);
				}
			}
		});

		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setAdapter(mImageAdapter);
		mImageAdapter.setOnImageSelectorListener(this);

	}


	/**
	 * Create popup ListView
	 */
	private void createPopupFolderList() {
		Point point = ScreenUtils.getScreenSize(getActivity());
		int width = point.x;
		int height = (int) (point.y * (4.5f / 8.0f));
		mFolderPopupWindow = new ListPopupWindow(getActivity());
		mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		mFolderPopupWindow.setAdapter(mFolderAdapter);
		mFolderPopupWindow.setContentWidth(width);
		mFolderPopupWindow.setWidth(width);
		mFolderPopupWindow.setHeight(height);
		mFolderPopupWindow.setAnchorView(mPopupAnchorView);
		mFolderPopupWindow.setModal(true);
		mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				mFolderAdapter.setSelectIndex(i);

				final int index = i;
				final AdapterView v = adapterView;

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mFolderPopupWindow.dismiss();

						if (index == 0) {
							getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderControl.getLoaderCallback());
							mCategoryText.setText(R.string.mis_folder_all);
							if (showCamera()) {
								mImageAdapter.setShowCamera(true);
							} else {
								mImageAdapter.setShowCamera(false);
							}
						} else {
							Folder folder = (Folder) v.getAdapter().getItem(index);
							if (null != folder) {
								mImageAdapter.setData(folder.images);
								mCategoryText.setText(folder.name);
							}

						}

						mGridView.smoothScrollToPosition(0);
					}
				}, 100);

			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// load image data
//		getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);

		methodRequiresWRITEPermission();
	}


	@Override
	public void onResume() {
		super.onResume();
		mImageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mFolderPopupWindow != null) {
			if (mFolderPopupWindow.isShowing()) {
				mFolderPopupWindow.dismiss();
			}
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				if (mTmpFile != null) {
					if (mCallback != null) {
						mCallback.onCameraShot(mTmpFile);
					}
				}
			} else {
				// delete tmp file
				while (mTmpFile != null && mTmpFile.exists()) {
					boolean success = mTmpFile.delete();
					if (success) {
						mTmpFile = null;
					}
				}
			}
		}
	}


	private boolean showCamera() {
		return getArguments() == null || getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
	}

	private int selectMode() {
		return getArguments() == null ? MODE_MULTI : getArguments().getInt(EXTRA_SELECT_MODE);
	}


	@Override
	public boolean onCheck(int position, Image image, boolean isCheck) {
		if (isCheck) {
			boolean show = mCallback.onImageSelected(image.path);//单选时，选择其他图片时需要刷新页面
			if (MultiImageControl.getSingleton().getMode() == MultiImageControl.MODE_SINGLE) {
				mImageAdapter.notifyDataSetChanged();
			}
			return show;
		} else {
			return mCallback.onImageUnselected(image.path);
		}

	}

	@Override
	public void onItemClick(int position, Image image, ArrayList<Image> datas) {
//		Toast.makeText(getContext(), "点击大图  " + position, Toast.LENGTH_SHORT).show();

		if (showCamera()) {
			position -= 1;
		}
		Intent intent = new Intent(getContext(), LargeImageActivity.class);
		intent.putParcelableArrayListExtra("Data", datas);
		intent.putExtra("image", image);
		intent.putExtra("position", position);
		int folderIndex = mFolderAdapter.getSelectIndex();
		intent.putExtra("folderIndex", folderIndex);//文件夹的位置
		startActivity(intent);

	}

	@Override
	public void onCamera() {
		methodRequiresCameraPermission();
	}

	private static final int RC_WRITE_EXTERNAL_PERMISSION = 222;

	@AfterPermissionGranted(RC_WRITE_EXTERNAL_PERMISSION)
	private void methodRequiresWRITEPermission() {
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (EasyPermissions.hasPermissions(getContext(), perms)) {
			// Already have permission, do the thing
			getActivity().getSupportLoaderManager().initLoader(LoadControl.LOADER_ALL, null, mLoaderControl.getLoaderCallback());
		} else {
			// Do not have permissions, request them now  getString(R.string.camera_and_wifi_rationale)
			EasyPermissions.requestPermissions(this, "获取读写权限",
					RC_WRITE_EXTERNAL_PERMISSION, perms);
		}
	}


	private static final int RC_CAMERA_PERMISSION = 111;

	@AfterPermissionGranted(RC_CAMERA_PERMISSION)
	private void methodRequiresCameraPermission() {
		String[] perms = {Manifest.permission.CAMERA};
		if (EasyPermissions.hasPermissions(getContext(), perms)) {
			// Already have permission, do the thing
			showCameraAction();
		} else {
			// Do not have permissions, request them now  getString(R.string.camera_and_wifi_rationale)
			EasyPermissions.requestPermissions(this, "获取相机权限",
					RC_CAMERA_PERMISSION, perms);
		}
	}

	/**
	 * Open camera
	 */
	private void showCameraAction() {

//		自定义相机
		Intent intent = new Intent(getActivity(), MisCameraActivity.class);
		startActivityForResult(intent, REQUEST_CAMERA);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getSupportLoaderManager().destroyLoader(mLoaderControl.getLoaderID());
	}

	@Override
	public void loadFinish(List<Image> images, ArrayList<Folder> mResultFolder) {
		mImageAdapter.setData(images);
		mFolderAdapter.setData(mResultFolder);
	}

	/**
	 * Callback for host activity
	 */
	public interface Callback {
		boolean onImageSelected(String path);

		boolean onImageUnselected(String path);

		void onCameraShot(File imageFile);
	}
}
