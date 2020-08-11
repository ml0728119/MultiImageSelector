package com.hxqc.multi_image_selector;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.hxqc.multi_image_selector.bean.Folder;
import com.hxqc.multi_image_selector.bean.Image;

/**
 * Created 胡俊杰
 * 2017/6/21.
 * Todo:
 */

public class LoadControl {
	// loaders
	public static final int LOADER_ALL = 0;

	public static final int LOADER_CATEGORY = 1;
	private boolean hasFolderGened = false;
	// image result data set
	private LinkedHashSet<String> resultList;
	// folder result data set
	private ArrayList<Folder> mResultFolder = new ArrayList<>();

	private Context context;

	public LoadControl(Context context) {
		this.context = context;
	}

	public LoaderManager.LoaderCallbacks<Cursor> getLoaderCallback() {
		return mLoaderCallback;
	}

	int loadID = -1;

	public int getLoaderID() {
		return loadID;
	}

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		private final String[] IMAGE_PROJECTION = {
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_ADDED,
				MediaStore.Images.Media.MIME_TYPE,
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media._ID};

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader cursorLoader = null;
			loadID = id;
			if (id == LOADER_ALL) {
				cursorLoader = new CursorLoader(context,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
						IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
						new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
			} else if (id == LOADER_CATEGORY) {
				cursorLoader = new CursorLoader(context,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
						IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
						null, IMAGE_PROJECTION[2] + " DESC");
			}
			return cursorLoader;
		}

		private boolean fileExist(String path) {
			if (!TextUtils.isEmpty(path)) {
				return new File(path).exists();
			}
			return false;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data != null) {
				if (data.getCount() > 0) {
					List<Image> images = new ArrayList<>();
					data.moveToFirst();
					do {
						String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
						String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
						long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
						if (!fileExist(path)) {
							continue;
						}
						Image image = null;
						if (!TextUtils.isEmpty(path)) {
							image = new Image(path, name, dateTime);
							images.add(image);
						}
						if (!hasFolderGened) {
							// get all folder data
							File folderFile = new File(path).getParentFile();
							if (folderFile != null && folderFile.exists()) {
								String fp = folderFile.getAbsolutePath();
								Folder f = getFolderByPath(fp);
								if (f == null) {
									Folder folder = new Folder();
									folder.name = folderFile.getName();
									folder.path = fp;
									folder.cover = image;
									List<Image> imageList = new ArrayList<>();
									imageList.add(image);
									folder.images = imageList;
									mResultFolder.add(folder);
								} else {
									f.images.add(image);
								}
							}
						}

					} while (data.moveToNext());

					if (mOnLoadFinishListener != null) {
						mOnLoadFinishListener.loadFinish(images, mResultFolder);
					}

				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	private Folder getFolderByPath(String path) {
		if (mResultFolder != null) {
			for (Folder folder : mResultFolder) {
				if (TextUtils.equals(folder.path, path)) {
					return folder;
				}
			}
		}
		return null;
	}

	private OnLoadFinishListener mOnLoadFinishListener;

	public interface OnLoadFinishListener {
		void loadFinish(List<Image> images, ArrayList<Folder> mResultFolder);
	}

	public void setOnLoadFinishListener(OnLoadFinishListener mOnLoadFinishListener) {
		this.mOnLoadFinishListener = mOnLoadFinishListener;
	}
}
