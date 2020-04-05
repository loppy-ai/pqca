package com.example.l.pqca

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Board(bitmap1: Bitmap, bitmap2: Bitmap){
    private var nextBoardPuyoColor = IntArray(8)
    private var nowBoardPuyoColor = IntArray(48)

    // インスタンス化した際にbitmapから盤面情報を生成する
    init {
        val nextAndNow1 = initialize(bitmap1)
        val nextAndNow2 = initialize(bitmap2)
        convertNext(nextAndNow1.first, nextAndNow2.first)
        convertNow(nextAndNow1.second, nextAndNow2.second)
    }

    // 画像→盤面ごとの色情報
    private fun initialize(bitmap: Bitmap): Pair<IntArray, IntArray> {
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
        return Pair(nextBoardPixelColor, nowBoardPixelColor)
    }

    fun getNext(): IntArray = nextBoardPuyoColor
    fun getNow(): IntArray = nowBoardPuyoColor

    // 盤面ごとの色情報→ネクストぷよ種別
    private fun convertNext(nextBoardPixelColor1: IntArray, nextBoardPixelColor2: IntArray) {
        //  1:赤   2:青   3:緑   4:黄   5:紫
        //  6:邪   7:固
        // 11:赤C 12:青C 13:緑C 14:黄C 15:紫C
        val checkArray = checkIsChance(nextBoardPixelColor1, nextBoardPixelColor2)
        for (i in nextBoardPixelColor1.indices) {
            if (checkArray[i]) {
                // チャンスぷよである
                val colorRed1 = Color.red(nextBoardPixelColor1[i])
                val colorGreen1 = Color.green(nextBoardPixelColor1[i])
                val colorBlue1 = Color.blue(nextBoardPixelColor1[i])
                val colorRed2 = Color.red(nextBoardPixelColor2[i])
                val colorGreen2 = Color.green(nextBoardPixelColor2[i])
                val colorBlue2 = Color.blue(nextBoardPixelColor2[i])
                // Log.d("next$i RGB Data1", "$colorRed1 $colorGreen1 $colorBlue1")
                // Log.d("next$i RGB Data2", "$colorRed2 $colorGreen2 $colorBlue2")
                // Log.d("border..........", "-----------")
                val colorRed = min(colorRed1, colorRed2)
                val colorGreen = min(colorGreen1, colorGreen2)
                val colorBlue = min(colorBlue1, colorBlue2)
                var max = colorRed
                if (max < colorGreen) max = colorGreen
                if (max < colorBlue) max = colorBlue
                when (max) {
                    colorRed   ->
                        when{
                            (colorGreen - colorBlue) < 50 -> nextBoardPuyoColor[i] = 11     // 赤
                            else                          -> nextBoardPuyoColor[i] = 14     // 黄
                        }
                    colorGreen -> nextBoardPuyoColor[i] = 13                                // 緑
                    colorBlue  ->
                        when{
                            colorRed < colorGreen -> nextBoardPuyoColor[i] = 12             // 青
                            else                  -> nextBoardPuyoColor[i] = 15             // 紫
                        }
                }
            }
            else {
                // チャンスぷよ/プリズムボールでない
                val colorRed = Color.red(nextBoardPixelColor1[i])
                val colorGreen = Color.green(nextBoardPixelColor1[i])
                val colorBlue = Color.blue(nextBoardPixelColor1[i])
                // Log.d("next$i RGB Data", "$colorRed/$colorGreen/$colorBlue")
                when (colorRed) {
                    in 0 until 60 ->
                        when (colorBlue) {
                            in   0 until 130 -> nextBoardPuyoColor[i] = 3   // 緑
                            else             -> nextBoardPuyoColor[i] = 2   // 青
                        }
                    in  60 until 150 -> nextBoardPuyoColor[i] = 5           // 紫
                    else ->
                        when (colorGreen) {
                            in   0 until  85 -> nextBoardPuyoColor[i] = 1   // 赤
                            else             -> nextBoardPuyoColor[i] = 4   // 黄
                        }
                }
            }
        }
        Log.d("next  Color...", "${nextBoardPuyoColor[0]} ${nextBoardPuyoColor[1]} ${nextBoardPuyoColor[2]} ${nextBoardPuyoColor[3]} ${nextBoardPuyoColor[4]} ${nextBoardPuyoColor[5]} ${nextBoardPuyoColor[6]} ${nextBoardPuyoColor[7]}")
        Log.d("border........","---------------")
    }

    // 盤面ごとの色情報→現在ぷよ種別
    private fun convertNow(nowBoardPixelColor1: IntArray, nowBoardPixelColor2: IntArray) {
        //  1:赤   2:青   3:緑   4:黄   5:紫
        //  6:邪   7:固   8:ハート
        // 11:赤C 12:青C 13:緑C 14:黄C 15:紫C 16:プリズム
        val checkArray = checkIsChance(nowBoardPixelColor1, nowBoardPixelColor2)
        for (i in nowBoardPixelColor1.indices) {
            if (checkArray[i]){
                val colorRed1 = Color.red(nowBoardPixelColor1[i])
                val colorGreen1 = Color.green(nowBoardPixelColor1[i])
                val colorBlue1 = Color.blue(nowBoardPixelColor1[i])
                val colorRed2 = Color.red(nowBoardPixelColor2[i])
                val colorGreen2 = Color.green(nowBoardPixelColor2[i])
                val colorBlue2 = Color.blue(nowBoardPixelColor2[i])
                // Log.d("now$i RGB Data1", "$colorRed1 $colorGreen1 $colorBlue1")
                // Log.d("now$i RGB Data2", "$colorRed2 $colorGreen2 $colorBlue2")
                // Log.d("border.........", "-----------")
                val colorRed = min(colorRed1, colorRed2)
                val colorGreen = min(colorGreen1, colorGreen2)
                val colorBlue = min(colorBlue1, colorBlue2)
                var min = colorRed
                if (min > colorGreen) min = colorGreen
                if (min > colorBlue) min = colorBlue

                when (min) {
                    colorRed   ->
                        when(colorRed) {
                            in   0 until 165 -> nowBoardPuyoColor[i] = 12          // 青
                            else             -> nowBoardPuyoColor[i] = 16          // プリズム
                        }
                    colorGreen -> nowBoardPuyoColor[i] = 15                        // 紫
                    colorBlue  ->
                        when (colorGreen - colorBlue) {
                            in   0 until  50 -> nowBoardPuyoColor[i] = 11          // 赤
                            else ->
                                when (colorRed) {
                                    in 250 .. 255 -> nowBoardPuyoColor[i] = 14     // 黄
                                    else          -> nowBoardPuyoColor[i] = 13     // 緑
                                }
                        }
                }
            }
            else {
                val colorRed = Color.red(nowBoardPixelColor1[i])
                val colorGreen = Color.green(nowBoardPixelColor1[i])
                val colorBlue = Color.blue(nowBoardPixelColor1[i])
                // Log.d("now$i RGB Data", "$colorRed/$colorGreen/$colorBlue")
                //  0  1  2  3  4  5  6  7
                //  8  9 10 11 12 13 14 15
                // 16 17 18 19 20 21 22 23
                // 24 25 26 27 28 29 30 31
                // 32 33 34 35 36 37 38 39
                // 40 41 42 43 44 45 46 47
                when (colorRed) {
                    in   0 until  65 -> nowBoardPuyoColor[i] = 2                   // 青
                    in  65 until 210 ->
                        when (colorBlue) {
                            in   0 until 100 -> nowBoardPuyoColor[i] = 3           // 緑
                            in 100 until 168 -> nowBoardPuyoColor[i] = 7           // 固ぷよ
                            in 168 until 220 -> nowBoardPuyoColor[i] = 6           // おじゃま
                            else             -> nowBoardPuyoColor[i] = 5           // 紫
                        }
                    else ->
                        when (colorBlue) {
                            in   0 until 135 ->
                                when (colorGreen) {
                                    in   0 until 135 -> nowBoardPuyoColor[i] = 1   // 赤
                                    else             -> nowBoardPuyoColor[i] = 4   // 黄
                                }
                            in 135 ..    255 ->
                                when (colorGreen) {
                                    in   0 until 200 -> nowBoardPuyoColor[i] = 8   // ハート
                                    else             -> nowBoardPuyoColor[i] = 16  // プリズム
                                }
                        }
                }
            }
        }
        for (h in 0 until 6) {
            Log.d("now   Color...", "${nowBoardPuyoColor[h*8]} ${nowBoardPuyoColor[h*8+1]} ${nowBoardPuyoColor[h*8+2]} ${nowBoardPuyoColor[h*8+3]} ${nowBoardPuyoColor[h*8+4]} ${nowBoardPuyoColor[h*8+5]} ${nowBoardPuyoColor[h*8+6]} ${nowBoardPuyoColor[h*8+7]}")
        }
    }

    // 2枚の画像の差分からチャンスぷよ（プリズムボール）かどうか確認
    private fun checkIsChance(boardPixelColor1: IntArray, boardPixelColor2: IntArray): BooleanArray{
        val checkArray = BooleanArray(boardPixelColor1.size)
        val threshold = 2
        for (i in boardPixelColor1.indices) {
            val colorRed1 = Color.red(boardPixelColor1[i])
            val colorGreen1 = Color.green(boardPixelColor1[i])
            val colorBlue1 = Color.blue(boardPixelColor1[i])
            val colorRed2 = Color.red(boardPixelColor2[i])
            val colorGreen2 = Color.green(boardPixelColor2[i])
            val colorBlue2 = Color.blue(boardPixelColor2[i])
            checkArray[i] = !((abs(colorRed1   - colorRed2  ) < threshold) and
                    (abs(colorGreen1 - colorGreen2) < threshold) and
                    (abs(colorBlue1  - colorBlue2 ) < threshold))     // 変化がなければFALSE, 変化があればTRUE
            //Log.d("now$i RGB Data1", "$colorRed1 $colorGreen1 $colorBlue1")
            //Log.d("now$i RGB Data2", "$colorRed2 $colorGreen2 $colorBlue2")
            //Log.d("border.........", "-----------")
        }
        return checkArray
    }
}

