package me.nereo.multi_image_selector;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import me.nereo.multi_image_selector.adapter.LargeViewPageAdapter;
import me.nereo.multi_image_selector.bean.Image;

public class LargeImageActivity extends AppCompatActivity {

	ViewPager mViewPage;
	LargeViewPageAdapter mViewPageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_large_image);

		ArrayList<Image> datas = getIntent().getParcelableArrayListExtra("Data");
		int position = getIntent().getIntExtra("position", 0);


		mViewPage = (ViewPager) findViewById(R.id.large_viewpage);
		mViewPageAdapter = new LargeViewPageAdapter(this, datas);
		mViewPage.setAdapter(mViewPageAdapter);
		mViewPage.setCurrentItem(position);
	}
}
