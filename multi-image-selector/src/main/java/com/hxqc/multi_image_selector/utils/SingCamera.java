//package me.nereo.multi_image_selector.utils;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.widget.Toast;
//
//import java.io.IOException;
//
//import com.hxqc.multi_image_selector.R;
//
///**
// * Created 胡俊杰
// * 2016/12/8.
// * Todo:
// */
//
//public class SingCamera {
//	/**
//	 * Open camera
//	 */
//	private void showCameraAction(Context context) {
//		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//				!= PackageManager.PERMISSION_GRANTED) {
//			requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//					context.getString(R.string.mis_permission_rationale_write_storage),
//					REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
//		} else {
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			if (intent.resolveActivity(context.getPackageManager()) != null) {
//				try {
//					mTmpFile = FileUtils.createTmpFile(context);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				if (mTmpFile != null && mTmpFile.exists()) {
//					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
//					startActivityForResult(intent, REQUEST_CAMERA);
//				} else {
//					Toast.makeText(context, R.string.mis_error_image_not_exist, Toast.LENGTH_SHORT).show();
//				}
//			} else {
//				Toast.makeText(context, R.string.mis_msg_no_camera, Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//
//	private void requestPermission(final String permission, String rationale, final int requestCode) {
//		if (shouldShowRequestPermissionRationale(permission)) {
//			new AlertDialog.Builder(getContext())
//					.setTitle(R.string.mis_permission_dialog_title)
//					.setMessage(rationale)
//					.setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							requestPermissions(new String[]{permission}, requestCode);
//						}
//					})
//					.setNegativeButton(R.string.mis_permission_dialog_cancel, null)
//					.create().show();
//		} else {
//			requestPermissions(new String[]{permission}, requestCode);
//		}
//	}
//}
