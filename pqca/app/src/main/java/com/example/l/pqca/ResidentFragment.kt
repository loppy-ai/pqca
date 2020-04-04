package com.example.l.pqca

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ResidentFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    lateinit var residentView: View

    // Viewがロードされた際に行われる処理
    fun loadView(mainActivity: MainActivity): View? {
        this.mainActivity = mainActivity
        residentView = LayoutInflater.from(mainActivity).inflate(R.layout.fragment_resident, null)
        buttonSetting()
        return residentView
    }

    private fun buttonSetting() {
        // クリアボタン
        residentView.findViewById<Button>(R.id.clearBtn).setOnClickListener {

        }
        // SS取得ボタン
        residentView.findViewById<Button>(R.id.captureBtn).setOnClickListener {
            // 画像取得
            lateinit var bitmap1: Bitmap
            lateinit var bitmap2: Bitmap
            runBlocking {
                bitmap1 = mainActivity.getScreenShot()
                delay(250)
                bitmap2 = mainActivity.getScreenShot()
            }
            // 盤面情報に変換
            val board = Board(bitmap1, bitmap2)
            val next = board.getNext()
            val now = board.getNow()
            // 盤面情報の解析

            // 解析結果の表示
        }
        // 透明化ボタン
        residentView.findViewById<Button>(R.id.minimizeBtn).setOnClickListener {
            mainActivity.closeResidentWindow()
            mainActivity.openTransparencyWindow()
        }
        // 常駐終了ボタン
        residentView.findViewById<Button>(R.id.endBtn).setOnClickListener {
            mainActivity.closeResidentWindow()
        }
    }

}
