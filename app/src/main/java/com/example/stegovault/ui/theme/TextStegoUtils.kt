package com.example.stegovault.utils

private const val ZERO = '\u200B' // 0
private const val ONE  = '\u200C' // 1

private const val START = "\u200E\u200E\u200E"
private const val END   = "\u200F\u200F\u200F"

fun encodeTextStego(cover: String, secret: String): String {
    val bytes = secret.toByteArray(Charsets.UTF_8)
    val bits = StringBuilder()

    for (b in bytes) {
        val v = b.toInt() and 0xFF
        for (i in 7 downTo 0) { // MSB -> LSB
            val bit = (v shr i) and 1
            bits.append(if (bit == 0) ZERO else ONE)
        }
    }
    return cover + START + bits.toString() + END
}

fun decodeTextStego(stego: String): String {
    val s = stego.indexOf(START)
    val e = stego.indexOf(END)
    if (s == -1 || e == -1 || e <= s) return ""

    val hidden = stego.substring(s + START.length, e)

    val bitString = buildString {
        for (ch in hidden) {
            when (ch) {
                ZERO -> append('0')
                ONE  -> append('1')
            }
        }
    }

    if (bitString.length < 8) return ""

    val byteCount = bitString.length / 8
    val bytes = ByteArray(byteCount)

    for (i in 0 until byteCount) {
        val chunk = bitString.substring(i * 8, i * 8 + 8)
        bytes[i] = chunk.toInt(2).toByte()
    }

    return try {
        bytes.toString(Charsets.UTF_8)
    } catch (e1: Exception) {
        ""
    }
}
