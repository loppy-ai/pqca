package com.example.l.pqca

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1
        private const val REQUEST_CAPTURE = 2
    }
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var projection: MediaProjection
    private lateinit var imageReader: ImageReader
    private lateinit var virtualDisplay: VirtualDisplay
    private var dpScale = 0
    private var screenDensity = 0
    private var displayWidth = 0
    private var displayHeight = 0
    private lateinit var imageView: ImageView
    private lateinit var residentWindowManager: WindowManager
    private lateinit var transparencyWindowManager: WindowManager
    private lateinit var residentFragment: ResidentFragment
    private lateinit var transparencyFragment: TransparencyFragment
    private var isResidentAttach = false
    private var isTransparencyAttach = false

    // 画面の縦横サイズとdpを取得
    private fun checkDisplaySize() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        dpScale = resources.displayMetrics.density.toInt()
        screenDensity = displayMetrics.densityDpi
        displayWidth = displayMetrics.widthPixels
        displayHeight = 2160
    }

    // 権限の確認
    private fun checkPermission() {
        // オーバーレイ
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${this.packageName}"))
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
        // メディアプロジェクション
        mediaProjectionManager = getSystemService(Service.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CAPTURE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkDisplaySize()
        checkPermission()

        // 常駐開始ボタン
        startBtn.setOnClickListener {
            openResidentWindow()
        }

        // 常駐終了ボタン
        endBtn.setOnClickListener {
            closeResidentWindow()
            closeTransparencyWindow()
        }

        // 最小化ボタン
        minimizeBtn.setOnClickListener {
            moveTaskToBack(true)
        }

        // imageView
        imageView = findViewById(R.id.imageView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == com.example.l.pqca.MainActivity.Companion.REQUEST_CAPTURE) {
            if (resultCode == RESULT_OK) {
                projection = mediaProjectionManager.getMediaProjection(resultCode, data)
                imageReader = ImageReader.newInstance(
                    displayWidth,
                    displayHeight, PixelFormat.RGBA_8888, 1)
                virtualDisplay = projection.createVirtualDisplay(
                    "ScreenCapture",
                    displayWidth, displayHeight, screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.surface, null, null)
            }
        }
    }

    // 常駐ウインドウを開く
    fun openResidentWindow() {
        if (Settings.canDrawOverlays(this) && !isResidentAttach) {
            residentWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 20 * dpScale
            params.y = 135 * dpScale

            residentFragment = ResidentFragment()
            residentWindowManager.addView(residentFragment.loadView(this), params)
            isResidentAttach = true
        }
    }

    // 常駐ウインドウを閉じる
    fun closeResidentWindow() {
        if (isResidentAttach) {
            residentWindowManager.removeView(residentFragment.residentView)
            isResidentAttach = false
        }
    }

    // 透明ウインドウを開く
    fun openTransparencyWindow() {
        if (Settings.canDrawOverlays(this) && !isTransparencyAttach) {
            transparencyWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.START
            params.y = 170 * dpScale

            transparencyFragment = TransparencyFragment()
            transparencyWindowManager.addView(transparencyFragment.loadView(this), params)
            isTransparencyAttach = true
        }
    }

    // 透明ウインドウを閉じる
    fun closeTransparencyWindow() {
        if (isTransparencyAttach) {
            transparencyWindowManager.removeView(transparencyFragment.transparencyView)
            isTransparencyAttach = false
        }
    }

    // SS撮影
    fun getScreenShot(): Bitmap {
        val image = imageReader.acquireLatestImage()
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * displayWidth
        val bitmap = Bitmap.createBitmap(
            displayWidth + rowPadding / pixelStride,
            displayHeight, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        val trimmedBitmap = trimming((bitmap))
        imageView.setImageBitmap(trimmedBitmap)
        return trimmedBitmap
    }

    // SSの切り出し
    private fun trimming(bitmap: Bitmap): Bitmap {
        val boardStart = 1147
        val width = displayWidth
        val height = 826
        return Bitmap.createBitmap(bitmap, 0, boardStart, width, height, null ,true)
        // SSのサイズは1080*2160
        // 盤面は1147～1973 826
    }

    // デバッグ用 ピクセル単位の処理
    private fun getColorTest(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width*height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for(h in 0 until height) {
            for (w in 0 until width) {
                    if ((w % 135 == 75) or (h % 126 == 58)) {
                        val number = w + h * width
                        val pixel = pixels[number]
                        pixels[number] = Color.argb(
                            Color.alpha(pixel),
                            Color.red(0),
                            Color.green(0),
                            Color.blue(0)
                        )
                    }
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        saveBitmap(bitmap)
        return bitmap
    }

    // デバッグ用 取得したSSの保存
    private fun saveBitmap(bitmap: Bitmap) {
        // パーミッション取得処理をしていない
        // 使用時にはAndroidManifestに <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> を記載したのちに、
        // 設定からストレージへの権限を付与すること
        val dir = "/pqacImage"
        val file = File(Environment.getExternalStorageDirectory().path + dir)
        if(!file.exists()) file.mkdir()

        val date = Date()
        val fileNameDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN)
        val fileName = fileNameDate.format(date) + ".jpg"
        val attachName = file.absolutePath + "/" + fileName

        val fos = FileOutputStream(attachName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        val values = ContentValues()
        val resolver = contentResolver
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.TITLE, fileName)
        values.put("_data", attachName)
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeResidentWindow()
        virtualDisplay.release()
    }
}
