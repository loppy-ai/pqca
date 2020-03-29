package com.example.l.pqca

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

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
        //  1:赤   2:青   3:緑   4:黄   5:紫
        //  6:邪   7:固
        // 11:赤C 12:青C 13:緑C 14:黄C 15:紫C
        for (i in nextBoardPixelColor.indices) {
            val colorRed = Color.red(nextBoardPixelColor[i])
            val colorGreen = Color.green(nextBoardPixelColor[i])
            val colorBlue = Color.blue(nextBoardPixelColor[i])
            // Log.d("next$i RGB Data", "$colorRed/$colorGreen/$colorBlue")
            when (colorRed) {
                in   0 until  60 ->
                    when (colorBlue) {
                        in   0 until 130 -> nextBoardPuyoColor[i] = 3   // 緑  31/136/ 37
                        in 130 ..    255 -> nextBoardPuyoColor[i] = 2   // 青   9/ 51/224    9/ 53/217  10/ 53/218
                    }
                in  60 until 150 -> nextBoardPuyoColor[i] = 5           // 紫 101/ 27/189
                in 150 ..    255 ->
                    when (colorGreen) {
                        in  0 until  85 -> nextBoardPuyoColor[i] = 1    // 赤 194/ 32/ 29  200/ 30/ 27
                        in 85 ..    255 -> nextBoardPuyoColor[i] = 4    // 黄 201/143/ 17
                    }
            }
        }
        Log.d("next  Color...", "${nextBoardPuyoColor[0]} ${nextBoardPuyoColor[1]} ${nextBoardPuyoColor[2]} ${nextBoardPuyoColor[3]} ${nextBoardPuyoColor[4]} ${nextBoardPuyoColor[5]} ${nextBoardPuyoColor[6]} ${nextBoardPuyoColor[7]}")
        Log.d("border........","---------------")
    }

    private fun convertNow(nowBoardPixelColor: IntArray) {
        var row = -1
        //  1:赤   2:青   3:緑   4:黄   5:紫
        //  6:邪   7:固   8:ハ   9:プ
        // 11:赤C 12:青C 13:緑C 14:黄C 15:紫C
        for (i in nowBoardPixelColor.indices) {
            if (i%8 == 0 ) row += 1
            val colorRed = Color.red(nowBoardPixelColor[i])
            val colorGreen = Color.green(nowBoardPixelColor[i])
            val colorBlue = Color.blue(nowBoardPixelColor[i])
            // Log.d("now$i RGB Data", "$colorRed/$colorGreen/$colorBlue")
            //  0  1  2  3  4  5  6  7
            //  8  9 10 11 12 13 14 15
            // 16 17 18 19 20 21 22 23
            // 24 25 26 27 28 29 30 31
            // 32 33 34 35 36 37 38 39
            // 40 41 42 43 44 45 46 47
            when (colorRed) {
                in   0 until  65 -> nowBoardPuyoColor[row][i%8] = 2                 // 青
                in  65 until 210 ->
                    when (colorBlue) {
                        in   0 until 100 -> nowBoardPuyoColor[row][i%8] = 3         // 緑
                        in 100 until 168 -> nowBoardPuyoColor[row][i%8] = 7         // 固ぷよ
                        in 168 until 220 -> nowBoardPuyoColor[row][i%8] = 6         // おじゃま
                        in 220 ..    255 -> nowBoardPuyoColor[row][i%8] = 5         // 紫
                    }
                in 210 ..    255 ->
                    when (colorBlue) {
                        in   0 until 135 ->
                            when(colorGreen) {
                                in   0 until 135 -> nowBoardPuyoColor[row][i%8] = 1 // 赤
                                in 135 ..    255 -> nowBoardPuyoColor[row][i%8] = 4 // 黄
                            }
                        in 135 ..    255 -> nowBoardPuyoColor[row][i%8] = 8         // ハート
                    }
            }
        }
        for (i in 0 until 6) {
            Log.d("now   Color...", "${nowBoardPuyoColor[i][0]} ${nowBoardPuyoColor[i][1]} ${nowBoardPuyoColor[i][2]} ${nowBoardPuyoColor[i][3]} ${nowBoardPuyoColor[i][4]} ${nowBoardPuyoColor[i][5]} ${nowBoardPuyoColor[i][6]} ${nowBoardPuyoColor[i][7]}")
        }
    }
}

