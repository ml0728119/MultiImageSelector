package me.nereo.multi_image_selector;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.LinkedHashSet;

/**
 * Multi image selector
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 * Updated by nereo on 2016/5/18.
 */
public class MultiImageSelectorActivity extends AppCompatActivity
		implements MultiImageSelectorFragment.Callback {

	// Single choice
	public static final int MODE_SINGLE = 0;
	// Multi choice
	public static final int MODE_MULTI = 1;

	/**
	 * Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default
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
	/**
	 * Result data set，ArrayList&lt;String&gt;
	 */
	public static final String EXTRA_RESULT = "select_result";

	// Default image size
	private static final int DEFAULT_IMAGE_SIZE = 9;

	private LinkedHashSet<String> resultList;
	private Button mSubmitButton;
	private int mDefaultCount = DEFAULT_IMAGE_SIZE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.MIS_NO_ACTIONBAR);
		setContentView(R.layout.mis_activity_default);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.BLACK);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		final Intent intent = getIntent();
		mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
		final int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
		final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
		resultList = MultiImageSelector.getSingleton().getChooseValue();

		mSubmitButton = (Button) findViewById(R.id.commit);
		if (mode == MODE_MULTI) {
			updateDoneText(resultList);
			mSubmitButton.setVisibility(View.VISIBLE);
			mSubmitButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (resultList != null && resultList.size() > 0) {

						MultiImageSelector.getSingleton().commit(MultiImageSelectorActivity.this);
					} else {
						setResult(RESULT_CANCELED);
					}
					finish();
				}
			});
		} else {
			mSubmitButton.setVisibility(View.GONE);
		}

		if (savedInstanceState == null) {
			Bundle bundle = new Bundle();
			bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
			bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
			bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
					.commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateDoneText(MultiImageSelector.getSingleton().getChooseValue());
	}

	/**
	 * Update done button by select image data
	 *
	 * @param resultList selected image data
	 */
	private void updateDoneText(LinkedHashSet<String> resultList) {
		int size = 0;
		if (resultList == null || resultList.size() <= 0) {
			mSubmitButton.setText(R.string.mis_action_done);
			mSubmitButton.setEnabled(false);
		} else {
			size = resultList.size();
			mSubmitButton.setEnabled(true);
		}
		mSubmitButton.setText(getString(R.string.mis_action_button_string,
				getString(R.string.mis_action_done), size, mDefaultCount));
	}
//
//	@Override
//	public void onSingleImageSelected(String path) {
//		Log.i("Tag", "onSingleImageSelected ");
//		MultiImageSelector.getSingleton().addResultImage(this, path);
//	}

	@Override
	public void onImageSelected(String path) {
		Log.i("Tag", "onImageSelected ");
		MultiImageSelector.getSingleton().addResultImage(this, path);
		updateDoneText(resultList);
	}

	@Override
	public void onImageUnselected(String path) {
		Log.i("Tag", "onImageUnselected 11111111");
		MultiImageSelector.getSingleton().removeResultImage(path);
		updateDoneText(resultList);
	}

	@Override
	public void onCameraShot(File imageFile) {
		Log.i("Tag", "onCameraShot  ");
		if (imageFile != null) {
			// notify system the image has change
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

			MultiImageSelector.getSingleton().addResultImage(this, imageFile.getAbsolutePath());
			MultiImageSelector.getSingleton().commit(MultiImageSelectorActivity.this);
		}
	}


}
