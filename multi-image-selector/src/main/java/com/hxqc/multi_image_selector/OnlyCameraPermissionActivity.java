package com.hxqc.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.io.File;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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

	final static int RC_CAMERA=1000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		methodRequiresTwoPermission();
	}


	@AfterPermissionGranted(RC_CAMERA)
	private void methodRequiresTwoPermission() {
		String[] perms = {Manifest.permission.CAMERA};
		if (EasyPermissions.hasPermissions(this, perms)) {
			showCameraAction();
			finish();
		} else {
			// Do not have permissions, request them now
			EasyPermissions.requestPermissions(this, "请设置照相机权限",
					RC_CAMERA, perms);
		}
	}

	private void showCameraAction() {
		Intent intent = new Intent(this, MisCameraActivity.class);
		startActivityForResult(intent, REQUEST_CAMERA);

	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				if (mTmpFile != null) {
					// notify system the image has change
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
					MultiImageSelector.multiImageControl.addResultImage(this, mTmpFile.getAbsolutePath());
					MultiImageSelector.multiImageControl.commit(this);
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
