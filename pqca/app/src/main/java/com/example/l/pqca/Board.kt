package com.example.l.pqca

import android.graphics.Bitmap

class Board(bitmap: Bitmap){
    private var nextBoardPuyoColor = IntArray(8)
    private var nowBoardPuyoColor = Array(6) { IntArray(8) }

    // インスタンス化した際にbitmapから盤面情報を生成する
    init {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        val nextBoardPixelColor = IntArray(8)
        val nowBoardPixelColor = IntArray(48)
        var count = 0
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (h in 0 until height) {
            for (w in 0 until width) {
                if ((w % 135 == 75) and (h % 126 == 58)) {
                    val number = w + h * width
                    val pixelColor = pixels[number]
                    if (count < 8) nextBoardPixelColor[count] = pixelColor
                    else           nowBoardPixelColor[count - 8] = pixelColor
                    count += 1
                }
            }
        }
        convertNext(nextBoardPixelColor)
        convertNow(nowBoardPixelColor)
    }

    fun getNext(): IntArray = nextBoardPuyoColor
    fun getNow(): Array<IntArray> = nowBoardPuyoColor

    private fun convertNext(nextBoardPixelColor: IntArray) {

    }

    private fun convertNow(nowBoardPixelColor: IntArray) {

    }
}

