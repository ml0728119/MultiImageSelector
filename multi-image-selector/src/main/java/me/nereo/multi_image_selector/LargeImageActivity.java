package me.nereo.multi_image_selector;

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

import java.util.ArrayList;

import me.nereo.multi_image_selector.adapter.LargeViewPageAdapter;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.view.SubmitButton;

public class LargeImageActivity extends AppCompatActivity {

	ViewPager mViewPage;
	LargeViewPageAdapter mViewPageAdapter;
	private SubmitButton mSubmitButton;
	ArrayList<Image> datas;
	CheckBox mCheckBox;

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

		datas = getIntent().getParcelableArrayListExtra("Data");
		int position = getIntent().getIntExtra("position", 0);

		mSubmitButton = (SubmitButton) findViewById(R.id.commit);
		mSubmitButton.updateDoneText();
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MultiImageControl.getSingleton().commit(LargeImageActivity.this);
			}
		});
		mCheckBox = (CheckBox) findViewById(R.id.checkmark);
		checkState(position);

		mViewPage = (ViewPager) findViewById(R.id.large_viewpage);
		mViewPageAdapter = new LargeViewPageAdapter(this, datas);
		mViewPage.setAdapter(mViewPageAdapter);
		mViewPage.setCurrentItem(position);
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

					boolean add = MultiImageControl.getSingleton().addResultImage(LargeImageActivity.this, datas.get(position).path);
					if (!add) {
						mCheckBox.setChecked(false);
					}
				} else {
					MultiImageControl.getSingleton().removeResultImage(datas.get(position).path);
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
		String path = datas.get(position).path;
		if (MultiImageControl.getSingleton().getChooseValue().contains(path)) {
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

}
