package com.example.l.pqca

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

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
/*
            TODO 時間差で2枚撮った差分を見てチャンスぷよかどうかを判断したい
            var bitmap1: Bitmap
            var bitmap2: Bitmap
            val thread = Thread(Runnable {
                try {
                    bitmap1 = mainActivity.getScreenShot()
                    Thread.sleep(500)
                    bitmap2 = mainActivity.getScreenShot()
                }catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            })
            thread.start()
 */
            val bitmap = mainActivity.getScreenShot()
            // 盤面情報に変換
            val board = Board(bitmap)
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
