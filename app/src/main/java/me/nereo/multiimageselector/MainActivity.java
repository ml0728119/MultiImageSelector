package me.nereo.multiimageselector;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hxqc.multi_image_selector.MultiImageSelector;
import com.hxqc.multi_image_selector.utils.LoadImage;

import java.util.ArrayList;
import java.util.Collection;


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


		mImageView = (ImageView) findViewById(R.id.image);
//		Log.i("Tag", "onCreate");
		mResultText = (TextView) findViewById(R.id.result);
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


	private void pickImage() {
		Log.i("Tag", "111111111111");
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
//				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//				!= PackageManager.PERMISSION_GRANTED) {
//			requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//					getString(R.string.mis_permission_rationale),
//					REQUEST_STORAGE_READ_ACCESS_PERMISSION);
//		} else {
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
		MultiImageSelector selector = new MultiImageSelector(this);
		selector.showCamera(showCamera).cropPhoto(true);
		selector.count(maxNum);
//		selector.coverView(R.layout.cover_layout);
		selector.coverView(LayoutInflater.from(this).inflate(R.layout.cover_layout,null,false));
		if (mChoiceMode.getCheckedRadioButtonId() == R.id.single) {
			selector.count(1);
		}

//		selector.onlyCamera();
		selector.start(MainActivity.this, new MultiImageSelector.MultiImageCallBack() {
			@Override
			public void multiSelectorImages(Collection<String> result) {


//					for (String s : result) {
//						Log.i("Tag", "123  " + s);
//						mSelectPath.add(s);
//					}

				for (String s : mSelectPath) {
					Log.i("Tag", "345  " + s + "   " + mSelectPath.hashCode());
				}

				for (String s : result) {
					Message message = new Message();
					Bundle bundle = new Bundle();
					Log.i("Tag", "call back1  " + s);
					bundle.putString("data", s);
					message.setData(bundle);
					handler.sendMessage(message);
				}
			}
		});
//		}
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

			LoadImage.loadImage(MainActivity.this,mImageView,msg.getData().getString("data"));
			return false;
		}
	});

	public void onclick(View view) {
//		startPhotoZoom("a", 1, 1);
//		.cropPhoto(true);
		MultiImageSelector selector = new MultiImageSelector(this);
		selector.showCamera(true).cropPhoto(false).onlyCamera();
		selector.count(1);
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
		}).setOnCancelListener(new MultiImageSelector.MultiCancelListener() {
			@Override
			public void onCancel() {
			}
		});
	}


}
