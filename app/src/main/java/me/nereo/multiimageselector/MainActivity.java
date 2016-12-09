package me.nereo.multiimageselector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import me.nereo.multi_image_selector.MultiImageSelector;

import static me.nereo.multiimageselector.R.id.result;


public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_IMAGE = 2;
	protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
	protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

	private TextView mResultText;
	private RadioGroup mChoiceMode, mShowCamera;
	private EditText mRequestNum;

	private ArrayList<String> mSelectPath = new ArrayList<>();

	ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView= (ImageView) findViewById(R.id.image);
		Log.i("Tag", "onCreate");
		mResultText = (TextView) findViewById(result);
		mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
		mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
		mRequestNum = (EditText) findViewById(R.id.request_num);

		mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				if (checkedId == R.id.multi) {
					mRequestNum.setEnabled(true);
				} else {
					mRequestNum.setEnabled(false);
					mRequestNum.setText("");
				}
			}
		});

		View button = findViewById(R.id.button);
		if (button != null) {
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					pickImage();
				}
			});
		}

	}

	MultiImageSelector selector;

	private void pickImage() {
		Log.i("Tag", "111111111111");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
					getString(R.string.mis_permission_rationale),
					REQUEST_STORAGE_READ_ACCESS_PERMISSION);
		} else {
			Log.i("Tag", "222222222");
			boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;
			int maxNum = 9;

			if (!TextUtils.isEmpty(mRequestNum.getText())) {
				try {
					maxNum = Integer.valueOf(mRequestNum.getText().toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			selector = new MultiImageSelector(this);
			selector.showCamera(showCamera);
			selector.count(maxNum);
			if (mChoiceMode.getCheckedRadioButtonId() == R.id.single) {
				selector.count(1);
			}
			Log.i("Tag", "3333333333  " + "   " + mSelectPath.hashCode());
			selector.origin(mSelectPath);
			for (String s : mSelectPath) {
				Log.i("Tag", "0000  " + s);
			}
			Log.i("Tag", "4444444444");
			selector.start(MainActivity.this, new MultiImageSelector.MultiImageCallBack() {
				@Override
				public void multiSelectorImages(Collection<String> result) {


					for (String s : result) {
						Log.i("Tag", "123  " + s);
						mSelectPath.add(s);
					}

					for (String s : mSelectPath) {
						Log.i("Tag", "345  " + s + "   " + mSelectPath.hashCode());
					}
				}
			});
		}
	}

	private void requestPermission(final String permission, String rationale, final int requestCode) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.mis_permission_dialog_title)
					.setMessage(rationale)
					.setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
						}
					})
					.setNegativeButton(R.string.mis_permission_dialog_cancel, null)
					.create().show();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				pickImage();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == REQUEST_IMAGE) {
//			if (resultCode == RESULT_OK) {
//				mSelectPath = data.getStringArrayListExtra(MultiImageControl.EXTRA_RESULT);
//				StringBuilder sb = new StringBuilder();
//				for (String p : mSelectPath) {
//					sb.append(p);
//					sb.append("\n");
//				}
//				mResultText.setText(sb.toString());
//			}
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private ArrayList<String> mSelectPath1 = new ArrayList<>();

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

//			startPhotoZoom(msg.getData().getString("data"));

			Glide.with(MainActivity.this)
					.load(msg.getData().getString("data"))
					.centerCrop()
					.placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
					.crossFade()
					.into(mImageView
					);
			return false;
		}
	});

	public void onclick(View view) {
//		startPhotoZoom("a", 1, 1);
		MultiImageSelector selector = new MultiImageSelector(this);
		selector.showCamera(false);
		selector.count(1).cropPhoto(true);
		selector.origin(mSelectPath1);
		selector.start(MainActivity.this, new MultiImageSelector.MultiImageCallBack() {
			@Override
			public void multiSelectorImages(Collection<String> result) {

				for (String s : result) {

					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("data", s);
					message.setData(bundle);
					handler.sendMessage(message);
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			final Uri resultUri = UCrop.getOutput(data);
			Log.i("Tag", "ss  re  " + resultUri);
		} else if (resultCode == UCrop.RESULT_ERROR) {
			final Throwable cropError = UCrop.getError(data);
			Log.e("Tag", "ss  re  " + cropError.toString());
		}
	}

	/**
	 * 裁剪图片方法实现
	 */
	public void startPhotoZoom(String aaa) {
		Uri from = Uri.parse("file://" + aaa);
		String path = getCropCacheFilePath();
		File file = new File(path);
		Uri to = Uri.fromFile(file);


//		Uri from = Uri.parse("file:///storage/emulated/0/DCIM/IMG_-1213758122.jpg");
//		Uri to = Uri.parse("file:///storage/emulated/0/Android/data/me.nereo.multiimageselector/cache/crop_1481254523167.jpg");

		Log.i("Tag", "from  " + from);
		Log.i("Tag", "to   " + to.toString());
		UCrop uCrop = UCrop.of(from, to);


//		uCrop.withAspectRatio(16, 9)
//				.withMaxResultSize(maxWidth, maxHeight)
//				.start(this);


		uCrop = uCrop.withAspectRatio(1, 1);
		UCrop.Options options = new UCrop.Options();
		options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
		options.setCompressionQuality(90);

		options.setHideBottomControls(true);
		options.setFreeStyleCropEnabled(true);
		uCrop.withOptions(options);
		uCrop.start(this);
	}

	protected final static int CODE_CROP_PHOTO = 102;    // 裁切图片

	public String getCropCacheFilePath() {
		Context context = this;
		String cropCachePath = String.valueOf(context.getExternalCacheDir()) + File.separator + "crop_" + System.currentTimeMillis() + ".png";
		return cropCachePath;
	}

	String TAG = "Tag";


}
