package com.example.l.pqca

import android.util.Log

class Analyze(next: IntArray, now: IntArray) {
    // initで分析して、フィールド変数に結果を入れる
    // Fragmentからそのフィールド変数にアクセスして結果を取得する

    init {
        pattern(next, now)
    }

    private fun pattern(firstNext: IntArray, firstNow: IntArray) {
        // パターン取得
        // val patterns = getPatterns()
        val patterns = Array(1){ intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1
        )}
        // パターンの数だけループ
        for (pattern in patterns) {
            // おじゃまとなぞり消しが衝突しているか確認 してたら次のパターンへ
            if (isCollision(firstNow, pattern)) continue
            // なぞり消しの適用 なぞったところを0にする
            val tracedNow = applyTrace(firstNow, pattern)
            // なぞり消したぷよを落とす 落ちてなくなったところを-1にする
            val droppedNow = dropNow(tracedNow)
            outputNowLog(droppedNow)

            // ここから連鎖終了までループ
            // 結合チェック 消えたところを0にする
            // 0がある ぷよを落とす 落ちてなくなったところを-1にする
            // 0がない ネクストを落とす
            // ネクストも落ちなくなったら連鎖終了

        }
    }

    private fun getPatterns(): Array<IntArray> {
        // TODO Realmにデータを格納しておき、読み出せるようにする
        // Array : 1個消しなので48 2個消しなら200
        // IntArray : 盤面なので48固定
        val patterns = Array(48) { IntArray(48) }
        // forDebug 1個消し用の2次元配列を作り出す
        // なぞり消す部分を1としてIntArrayに格納
        for (pattern in patterns.indices) {
            for (board in patterns[pattern].indices) {
                if (pattern == board) patterns[pattern][board] = 1
            }
/*
            Log.d("", "${patterns[pattern][0]}${patterns[pattern][1]}${patterns[pattern][2]}${patterns[pattern][3]}${patterns[pattern][4]}${patterns[pattern][5]}${patterns[pattern][6]}${patterns[pattern][7]}" +
                    "${patterns[pattern][8]}${patterns[pattern][9]}${patterns[pattern][10]}${patterns[pattern][11]}${patterns[pattern][12]}${patterns[pattern][13]}${patterns[pattern][14]}${patterns[pattern][15]}" +
                    "${patterns[pattern][16]}${patterns[pattern][17]}${patterns[pattern][18]}${patterns[pattern][19]}${patterns[pattern][20]}${patterns[pattern][21]}${patterns[pattern][22]}${patterns[pattern][23]}" +
                    "${patterns[pattern][24]}${patterns[pattern][25]}${patterns[pattern][26]}${patterns[pattern][27]}${patterns[pattern][28]}${patterns[pattern][29]}${patterns[pattern][30]}${patterns[pattern][31]}" +
                    "${patterns[pattern][32]}${patterns[pattern][33]}${patterns[pattern][34]}${patterns[pattern][35]}${patterns[pattern][36]}${patterns[pattern][37]}${patterns[pattern][38]}${patterns[pattern][39]}" +
                    "${patterns[pattern][40]}${patterns[pattern][41]}${patterns[pattern][42]}${patterns[pattern][43]}${patterns[pattern][44]}${patterns[pattern][45]}${patterns[pattern][46]}${patterns[pattern][47]}")
*/
        }
        return patterns
    }

    private fun isCollision(now: IntArray, pattern: IntArray): Boolean {
        var isCollision = false
        for (i in pattern.indices) {
            if (pattern[i] == 1) {
                if ((now[i] == 6) or (now[i] == 7)) {
                    isCollision = true
                    break
                }
            }
        }
        return isCollision
    }

    private fun applyTrace(now: IntArray, pattern: IntArray): IntArray{
        for (i in pattern.indices) {
            if (pattern[i] == 1) now[i] = 0
        }
        return now
    }

    private fun dropNow(now: IntArray): IntArray {
        // 0になっているところを落とす 落としたところは-1にする
        // 効率重視のため右下から順番に見ていく
        for (ri in now.lastIndex downTo 0) {
            // 0以外は次を見る
            if (now[ri] != 0) continue
            // 0だったら上(now[ri-8])を見る
            var target = ri - 8
            while (target >= 0) {
                // TODO 複数個まとめて落とすと効率化できそう
                // 1つ上が0以外だったら落として、1つ上を0にする
                if (now[target] != 0) {
                    now[ri] = now[target]
                    now[target] = 0
                    break
                }
                // 1つ上が0だったら更に上を見る
                target -= 8
            }
            // -1にする
            if (target < 0) {
                // 一番上から自分までを-1にする
                target += 8
                while (target <= ri) {
                    now[target] = -1
                    target += 8
                }
            }
        }
        // 落とした盤面を返す
        return now
    }

    private fun outputLog(next: IntArray, now: IntArray) {
        Log.d("next", "---------------")
        Log.d("next", "${next[0]} ${next[1]} ${next[2]} ${next[3]} ${next[4]} ${next[5]} ${next[6]} ${next[7]}")
        Log.d("now ", "---------------")
        for (h in 0 until 6) {
            Log.d("now ", "${now[h*8]} ${now[h*8+1]} ${now[h*8+2]} ${now[h*8+3]} ${now[h*8+4]} ${now[h*8+5]} ${now[h*8+6]} ${now[h*8+7]}")
        }
    }

    private fun outputNowLog(now: IntArray) {
        Log.d("now ", "---------------")
        for (h in 0 until 6) {
            Log.d("now ", "${now[h*8]} ${now[h*8+1]} ${now[h*8+2]} ${now[h*8+3]} ${now[h*8+4]} ${now[h*8+5]} ${now[h*8+6]} ${now[h*8+7]}")
        }
    }
}