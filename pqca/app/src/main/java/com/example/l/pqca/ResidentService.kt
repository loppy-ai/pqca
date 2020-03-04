package com.example.l.pqca

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Button
import androidx.core.app.NotificationCompat
import java.util.*

class ResidentService : Service() {
    private val notificationId = Random().nextInt()
    private lateinit var windowManager: WindowManager
    private lateinit var view: View

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    // 通知の設定
    private fun startNotification() {
        val channelId = packageName
        val channelName = getString(R.string.app_name)
        val channel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
        val notification = notificationBuilder.build()
        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val dpScale = resources.displayMetrics.density.toInt()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view = LayoutInflater.from(this).inflate(R.layout.service_resident, null, false)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.y = 135 * dpScale

        windowManager.addView(view, params)
        buttonSetting()
    }

    private fun buttonSetting(){
        // クリアボタン
        view.findViewById<Button>(R.id.clearBtn).setOnClickListener {

        }

        // SS取得ボタン
        view.findViewById<Button>(R.id.captureBtn).setOnClickListener {

        }

        // 最小化ボタン
        view.findViewById<Button>(R.id.minimizeBtn).setOnClickListener {

        }

        // 常駐終了ボタン
        view.findViewById<Button>(R.id.endBtn).setOnClickListener {
            stopSelf()
        }
    }

    override fun onDestroy() {
        windowManager.removeView(view)
        super.onDestroy()
    }
}
