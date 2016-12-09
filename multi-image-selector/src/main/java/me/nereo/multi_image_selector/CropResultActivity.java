package me.nereo.multi_image_selector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yalantis.ucrop.UCrop;

public class CropResultActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_result);
		String fromPath = getIntent().getStringExtra("fromPath");
		toCrop(CropResultActivity.this, fromPath);
	}

	String toFilePath = "/storage/emulated/0/Android/data/me.nereo.multiimageselector/cache/crop_1481254523167.jpg";

	void toCrop(Activity context, String fromPath) {
//		Uri from = Uri.parse("file:///storage/emulated/0/DCIM/IMG_-1213758122.jpg");
		Uri from = Uri.parse("file://" + fromPath);
		Uri to = Uri.parse("file://" + toFilePath);

		Log.i("Tag", "from  " + from);
		Log.i("Tag", "to   " + to.toString());
		UCrop uCrop = UCrop.of(from, to);
		uCrop = uCrop.withAspectRatio(1, 1);
		UCrop.Options options = new UCrop.Options();
		options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
		options.setCompressionQuality(90);
		options.setHideBottomControls(true);
		options.setFreeStyleCropEnabled(true);
		uCrop.withOptions(options);
		uCrop.start(context);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			final Uri resultUri = UCrop.getOutput(data);
			Log.i("Tag", "ss  re  " + resultUri);

			MultiImageControl.getSingleton().addResultImage(CropResultActivity.this, toFilePath);
			MultiImageControl.getSingleton().toFinish();
		} else if (resultCode == UCrop.RESULT_ERROR) {
			final Throwable cropError = UCrop.getError(data);
			Log.e("Tag", "ss  re  " + cropError.toString());
		}
		finish();
	}
}
