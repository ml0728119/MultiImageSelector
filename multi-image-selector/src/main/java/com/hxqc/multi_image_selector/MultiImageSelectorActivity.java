package com.hxqc.multi_image_selector;

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

import java.io.File;
import java.util.LinkedHashSet;

import static com.hxqc.multi_image_selector.MultiImageSelector.MultiImageControl.MODE_MULTI;

/**
 * Multi image selector
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 * Updated by nereo on 2016/5/18.
 */
public class MultiImageSelectorActivity extends AppCompatActivity
		implements MultiImageSelectorFragment.Callback {


	/**
	 * Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default
	 */
	public static final String EXTRA_SELECT_COUNT = "max_select_count";
	/**
	 * Select mode，by default
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
	private SubmitButton mSubmitButton;
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
		resultList = MultiImageSelector.multiImageControl.getChooseValue();

		mSubmitButton = (SubmitButton) findViewById(R.id.mis_commit);
		mSubmitButton.updateDoneText();
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (resultList != null && resultList.size() > 0) {
					MultiImageSelector.multiImageControl.commit(MultiImageSelectorActivity.this);
				}
				finish();
			}
		});


		if (savedInstanceState == null) {
			Bundle bundle = new Bundle();
			bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
			bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
			bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.image_layout, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
					.commit();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.i("Tag"," onBackPressed ");
		MultiImageSelector.multiImageControl.cancel();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Log.i("Tag"," onOptionsItemSelectedonOptionsItemSelected ");
				MultiImageSelector.multiImageControl.cancel();
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSubmitButton.updateDoneText();
	}


	@Override
	public boolean onImageSelected(String path) {
		boolean add = MultiImageSelector.multiImageControl.addResultImage(this, path);
		mSubmitButton.updateDoneText();
		return add;
	}

	@Override
	public boolean onImageUnselected(String path) {
		MultiImageSelector.multiImageControl.removeResultImage(path);
		mSubmitButton.updateDoneText();
		return true;
	}

	@Override
	public void onCameraShot(File imageFile) {
		if (imageFile != null) {
			// notify system the image has change
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
			MultiImageSelector.multiImageControl.addResultImage(this, imageFile.getAbsolutePath());
			MultiImageSelector.multiImageControl.commit(MultiImageSelectorActivity.this);
			finish();
		}
	}


}
