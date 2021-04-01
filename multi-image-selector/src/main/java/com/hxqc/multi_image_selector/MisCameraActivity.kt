package com.hxqc.multi_image_selector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_mis_camera.*
import java.io.*


/**
 * 说明:相机
 *
 * @author: 胡俊杰
 * @since: 2016-12-12
 * Copyright:恒信汽车电子商务有限公司
 */
class MisCameraActivity : Activity(), OnClickListener {
    internal lateinit var mCancelView: TextView
    internal lateinit var mFlashView: ImageView
    internal lateinit var mTakePhotoView: ImageView
    internal lateinit var mPreviewView: ImageView        //预览
    internal lateinit var mCommitView: TextView       //提交
    internal lateinit var mRepetitionView: TextView  //重拍

    internal lateinit var mPreviewLayout: RelativeLayout
    internal lateinit var mCameraLayout: RelativeLayout
    private var mCameraView: CameraView? = null
    internal lateinit var saveFilePath: String

    private var mBackgroundHandler: Handler? = null
    private var mCurrentFlash: Int = 0

    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView?) {
            //			Log.d(TAG, "onCameraOpened");
        }

        override fun onCameraClosed(cameraView: CameraView?) {
            //			Log.d(TAG, "onCameraClosed");
        }

        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
            //			Log.d(TAG, "onPictureTaken " + data.length);
            showPreview(data)

        }
    }

    private val backgroundHandler: Handler
        get() {
            if (mBackgroundHandler == null) {
                val thread = HandlerThread("background")
                thread.start()
                mBackgroundHandler = Handler(thread.looper)
            }
            return mBackgroundHandler as Handler
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置全屏无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        setContentView(R.layout.activity_mis_camera)
        initView()
    }

    protected fun initView() {
        mPreviewLayout = findViewById<View>(R.id.mis_preview_layout) as RelativeLayout
        mCameraLayout = findViewById<View>(R.id.mis_camera_layout) as RelativeLayout
        mCameraView = findViewById<View>(R.id.mis_camera) as CameraView
        if (mCameraView != null) {
            mCameraView!!.addCallback(mCallback)
        }
        mFlashView = findViewById<View>(R.id.mis_flash) as ImageView
        mCancelView = findViewById<View>(R.id.mis_cancel) as TextView

        mTakePhotoView = findViewById<View>(R.id.mis_take_photo) as ImageView
        mTakePhotoView.setOnClickListener(this)
        mCancelView.setOnClickListener(this)
        mFlashView.setOnClickListener(this)

        mPreviewView = findViewById<View>(R.id.mis_preview) as ImageView
        mCommitView = findViewById<View>(R.id.mis_commit) as TextView
        mRepetitionView = findViewById<View>(R.id.mis_repetition) as TextView
        if (MultiImageSelector.multiImageControl.mCoverView != null) {
            mis_cover_layout.addView(MultiImageSelector.multiImageControl.mCoverView)
        }
        if (MultiImageSelector.multiImageControl.mCoverLayoutID != 0) {
            mis_cover_layout.addView(LayoutInflater.from(this).inflate(MultiImageSelector.multiImageControl.mCoverLayoutID, null, false))
        }
    }

    override fun onClick(view: View) {
        val i = view.id
        if (i == R.id.mis_take_photo) {
            try {
                if (mCameraView != null) {
                    mCameraView!!.takePicture()
                }
            } catch (e: Exception) {
                if (mCameraView != null) {
                    mCameraView!!.start()
                }
                Toast.makeText(this, "相机初始化失败，重试", Toast.LENGTH_SHORT).show()
            }
        } else if (i == R.id.mis_cancel) {
            MultiImageSelector.multiImageControl.cancel()
            finish()
        } else if (i == R.id.mis_flash) {
            if (mCameraView != null) {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.size
                mFlashView.setBackgroundResource(FLASH_ICONS[mCurrentFlash])
                mCameraView!!.flash = FLASH_OPTIONS[mCurrentFlash]
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MultiImageSelector.multiImageControl.cancel()
    }

    override fun onResume() {
        super.onResume()
        try {
            mCameraView!!.start()
        } catch (e: Exception) {
            e.printStackTrace()

            val builder = AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("请前往系统设置，开启相机权限").setPositiveButton("确定"
                    ) { dialog, which -> finish() }
            builder.create().show()
        }

    }

    override fun onPause() {
        try {
            mCameraView!!.stop()
        } catch (e: Exception) {
            e.printStackTrace()
            val builder = AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("请前往系统设置，开启相机权限").setPositiveButton("确定")
                    { _, _ -> finish() }
            builder.create().show()
        }

        super.onPause()
    }


    private fun saveFile(data: ByteArray?) {
        backgroundHandler.post {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    System.currentTimeMillis().toString() + ".jpg")
            saveFilePath = file.path
            //				Log.d(TAG, "onPictureTaken " + file.getPath());
            var os: OutputStream? = null
            try {
                os = FileOutputStream(file)
                os.write(data!!)
                os.close()
            } catch (e: IOException) {
                //					Log.w(TAG, "Cannot write to " + file, e);
            } finally {
                if (os != null) {
                    try {
                        os.close()
                        MultiImageSelector.multiImageControl.addResultImage(this@MisCameraActivity, saveFilePath)
                        MultiImageSelector.multiImageControl.commit(this@MisCameraActivity)

                    } catch (e: IOException) {
                        // Ignore
                    } finally {
                        finish()
                    }
                    if (MultiImageSelector.multiImageControl.save2Gallery) {
                        file2Gallery(this, file)
                    }

                }
            }
        }

    }

    fun file2Gallery(context: Context, file: File) {

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.contentResolver,
                    file.absolutePath, file.name, null)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            val paths = arrayOf<String>(file.absolutePath)
            MediaScannerConnection.scanFile(context, paths, null, null)
        } else {
            val intent: Intent
            if (file.isDirectory) {
                intent = Intent(Intent.ACTION_MEDIA_MOUNTED)
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver")
                intent.data = Uri.fromFile(Environment.getExternalStorageDirectory())
            } else {
                intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = Uri.fromFile(file)
            }
            context.sendBroadcast(intent)
        }
    }

    private fun showPreview(data: ByteArray?) {
        mPreviewLayout.visibility = View.VISIBLE
        mCameraLayout.visibility = View.GONE

        mCommitView.setOnClickListener { saveFile(data) }
        mRepetitionView.setOnClickListener {
            mPreviewLayout.visibility = View.GONE
            mCameraLayout.visibility = View.VISIBLE
        }
        Glide.with(this).load(data).into(mPreviewView)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler!!.looper.quitSafely()
            } else {
                mBackgroundHandler!!.looper.quit()
            }
            mBackgroundHandler = null
        }
    }

    fun switch_camera(view: View) {
        if (mCameraView != null) {
            val facing = mCameraView!!.facing
            mCameraView!!.facing = if (facing == CameraView.FACING_FRONT)
                CameraView.FACING_BACK
            else
                CameraView.FACING_FRONT
        }
    }

    companion object {
        private val TAG = "MisCameraActivity"
        val PHOTO_PATH = "photo_path"
        private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF, CameraView.FLASH_ON)

        private val FLASH_ICONS = intArrayOf(R.drawable.ic_flash_auto, R.drawable.ic_flash_off, R.drawable.ic_flash_on)
    }

}
