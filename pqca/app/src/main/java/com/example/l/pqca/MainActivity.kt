package com.example.l.pqca

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1    // 何でもいいらしい？
    }

    // オーバーレイ権限の確認
    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, Companion.REQUEST_OVERLAY_PERMISSION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

        // 常駐開始ボタン
        startBtn.setOnClickListener {
            val intent = Intent(this, ResidentService::class.java)
            toastMake(getString(R.string.start_toast))
            startForegroundService(intent)
        }

        // 常駐終了ボタン
        endBtn.setOnClickListener {
            val intent = Intent(this, ResidentService::class.java)
            toastMake(getString(R.string.end_toast))
            stopService(intent)
        }

        // 最小化ボタン
        minimizeBtn.setOnClickListener {
            moveTaskToBack(true)
        }
    }

    // トーストの表示
    private fun toastMake(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 700)
        toast.show()
    }

}
