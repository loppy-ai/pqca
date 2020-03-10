package com.example.l.pqca

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class ResidentFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    lateinit var residentView: View

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
            val imageView = mainActivity.getScreenShot()
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
