package com.hxqc.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * 说明:相机
 *
 * @author: 胡俊杰
 * @since: 2017-7-1
 * Copyright:恒信汽车电子商务有限公司
 */
public class OnlyCameraPermissionActivity extends Activity {
	private static final int REQUEST_CAMERA = 100;
	private File mTmpFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		methodRequiresCameraPermission();
	}

	private void methodRequiresCameraPermission() {


		boolean a=	ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
		String[] perms = {Manifest.permission.CAMERA};
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				//申请WRITE_EXTERNAL_STORAGE权限
				ActivityCompat.requestPermissions(this, perms, 123);//自定义的code

			} else {
				showCameraAction();
			}
		} else {
			showCameraAction();
		}
	}

	private void showCameraAction() {
		Intent intent = new Intent(this, MisCameraActivity.class);
		startActivityForResult(intent, REQUEST_CAMERA);

	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		// Forward results to EasyPermissions
		if (permissions.length <= 0 || grantResults.length <= 0) return;
		if (requestCode == 123) {
			for (int i = 0; i < permissions.length; i++) {
				switch (permissions[i]) {
					case Manifest.permission.CAMERA:
						if (grantResults[i] == 0) {
							showCameraAction();
						}else {
							finish();
						}
						break;
				}
			}
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				if (mTmpFile != null) {
					// notify system the image has change
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
					MultiImageControl.getSingleton().addResultImage(this, mTmpFile.getAbsolutePath());
					MultiImageControl.getSingleton().commit(this);
				}
			} else {
				// delete tmp file
				while (mTmpFile != null && mTmpFile.exists()) {
					boolean success = mTmpFile.delete();
					if (success) {
						mTmpFile = null;
					}
				}
			}
			finish();
		}
	}


}
