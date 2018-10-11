package com.hxqc.multi_image_selector;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.hxqc.multi_image_selector.adapter.LargeViewPageAdapter;
import com.hxqc.multi_image_selector.bean.Folder;
import com.hxqc.multi_image_selector.bean.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LargeImageActivity extends AppCompatActivity implements LoadControl.OnLoadFinishListener {

	ViewPager mViewPage;
	LargeViewPageAdapter mViewPageAdapter;
	private SubmitButton mSubmitButton;
	ArrayList<Image> datas;
	CheckBox mCheckBox;
	LoadControl mLoaderControl;
	Image image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.MIS_NO_ACTIONBAR);
		setContentView(R.layout.activity_large_image);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.BLACK);
		}
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

//		datas = getIntent().getParcelableArrayListExtra("Data");

		image = getIntent().getParcelableExtra("image");
		int folderIndex = getIntent().getIntExtra("folderIndex", 0);
//		Log.i("Tag", "folderIndex --------- " + folderIndex);
		mLoaderControl = new LoadControl(this);
		mLoaderControl.setOnLoadFinishListener(this);
		Bundle bundle = new Bundle();
		bundle.putString("path", new File(image.path).getParentFile().getPath());
		getSupportLoaderManager().initLoader(folderIndex == 0 ? LoadControl.LOADER_ALL : LoadControl.LOADER_CATEGORY, bundle, mLoaderControl.getLoaderCallback());


		mSubmitButton = (SubmitButton) findViewById(R.id.mis_commit);
		mSubmitButton.updateDoneText();
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				MultiImageSelector.multiImageControl.commit(LargeImageActivity.this);
			}
		});
		mCheckBox = (CheckBox) findViewById(R.id.checkmark);
//

		mViewPage = (ViewPager) findViewById(R.id.large_viewpage);
		mViewPageAdapter = new LargeViewPageAdapter(this);
		mViewPage.setAdapter(mViewPageAdapter);

		mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				checkState(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});


		mCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = mViewPage.getCurrentItem();
				if (mCheckBox.isChecked()) {

					boolean add = MultiImageSelector.multiImageControl.addResultImage(LargeImageActivity.this, datas.get(position).path);
					if (!add) {
						mCheckBox.setChecked(false);
					}
				} else {
					MultiImageSelector.multiImageControl.removeResultImage(datas.get(position).path);
				}
				mSubmitButton.updateDoneText();
			}
		});
	}

	/**
	 * 修改选择框的状态
	 *
	 * @param position
	 */
	private void checkState(int position) {
		if (position == -1) return;
		String path = datas.get(position).path;
		if (MultiImageSelector.multiImageControl.getChooseValue().contains(path)) {
			mCheckBox.setChecked(true);
		} else {
			mCheckBox.setChecked(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getSupportLoaderManager().destroyLoader(mLoaderControl.getLoaderID());
	}

	@Override
	public void loadFinish(List<Image> images, ArrayList<Folder> mResultFolder) {
		this.datas = (ArrayList<Image>) images;
		int position = getIntent().getIntExtra("position", -1);
		mViewPageAdapter.setDatas((ArrayList<Image>) images);
		mViewPage.setCurrentItem(position);
		checkState(position);

	}
}
