package com.example.l.pqca

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class TransparencyFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    lateinit var transparencyView: View

    fun loadView(mainActivity: MainActivity): View? {
        this.mainActivity = mainActivity
        transparencyView = LayoutInflater.from(mainActivity).inflate(R.layout.fragment_transparency, null)
        buttonSetting()
        return transparencyView
    }

    private fun buttonSetting() {
        // 戻るボタン
        transparencyView.findViewById<Button>(R.id.returnBtn).setOnClickListener {
            mainActivity.closeTransparencyWindow()
            mainActivity.openResidentWindow()
        }
    }
}
