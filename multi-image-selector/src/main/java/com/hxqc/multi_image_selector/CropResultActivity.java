package com.hxqc.multi_image_selector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

public class CropResultActivity extends Activity {
	String toFilePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_result);
		String fromPath = getIntent().getStringExtra("fromPath");
		toCrop(CropResultActivity.this, fromPath);
	}

	void toCrop(Activity context, String fromPath) {
		Uri from = Uri.parse("file://" + fromPath);
		Uri to = toFilePath(context);

		UCrop uCrop = UCrop.of(from, to);
		uCrop = uCrop.withAspectRatio(MultiImageSelector.multiImageControl.getRatioX(), MultiImageSelector.multiImageControl.getRatioY());
		UCrop.Options options = new UCrop.Options();
		options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
		options.setCompressionQuality(90);
		options.setHideBottomControls(false);
		options.setFreeStyleCropEnabled(false);
		options.setToolbarColor(ContextCompat.getColor(this, R.color.mis_actionbar_color));
		options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
		uCrop.withOptions(options);
		uCrop.start(context);
	}

	private Uri toFilePath(Activity context) {
		File toFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "crop_" + System.currentTimeMillis() + ".jpg");
		toFilePath = toFile.getPath();

		return Uri.fromFile(toFile);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			final Uri resultUri = UCrop.getOutput(data);

			MultiImageSelector.multiImageControl.addResultImage(CropResultActivity.this, toFilePath);
			MultiImageSelector.multiImageControl.toFinish();
		} else if (resultCode == UCrop.RESULT_ERROR) {
			final Throwable cropError = UCrop.getError(data);
		}
		finish();
	}
}
