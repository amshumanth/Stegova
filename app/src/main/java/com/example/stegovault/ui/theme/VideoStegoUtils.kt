package com.example.stegovault.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder

private val MAGIC = "SVVID1".toByteArray(Charsets.US_ASCII)
private val MAGIC_END = "SVEND1".toByteArray(Charsets.US_ASCII)

// footer = MAGIC + [len(4 LE)] + msgBytes + MAGIC_END
fun encodeVideoFooter(videoBytes: ByteArray, secret: String): ByteArray {
    val msg = secret.toByteArray(Charsets.UTF_8)
    val len = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(msg.size).array()

    val out = ByteArray(videoBytes.size + MAGIC.size + 4 + msg.size + MAGIC_END.size)

    // copy original video
    System.arraycopy(videoBytes, 0, out, 0, videoBytes.size)

    var p = videoBytes.size
    System.arraycopy(MAGIC, 0, out, p, MAGIC.size); p += MAGIC.size
    System.arraycopy(len, 0, out, p, 4); p += 4
    System.arraycopy(msg, 0, out, p, msg.size); p += msg.size
    System.arraycopy(MAGIC_END, 0, out, p, MAGIC_END.size)

    return out
}

fun decodeVideoFooter(videoBytes: ByteArray): String {
    // Search for MAGIC in the last 512KB (fast + enough)
    val tailSize = minOf(videoBytes.size, 512 * 1024)
    val start = videoBytes.size - tailSize

    fun indexOfPattern(data: ByteArray, pattern: ByteArray, from: Int): Int {
        outer@ for (i in from until data.size - pattern.size + 1) {
            for (j in pattern.indices) {
                if (data[i + j] != pattern[j]) continue@outer
            }
            return i
        }
        return -1
    }

    // We search inside the tail array
    val tail = videoBytes.copyOfRange(start, videoBytes.size)
    val magicPos = indexOfPattern(tail, MAGIC, 0)
    if (magicPos == -1) return ""

    val lenPos = magicPos + MAGIC.size
    if (lenPos + 4 > tail.size) return ""

    val len = ByteBuffer.wrap(tail, lenPos, 4).order(ByteOrder.LITTLE_ENDIAN).int
    if (len <= 0) return ""

    val msgPos = lenPos + 4
    val endPos = msgPos + len
    if (endPos + MAGIC_END.size > tail.size) return ""

    // Verify end marker
    for (i in MAGIC_END.indices) {
        if (tail[endPos + i] != MAGIC_END[i]) return ""
    }

    val msgBytes = tail.copyOfRange(msgPos, endPos)
    return try { msgBytes.toString(Charsets.UTF_8) } catch (e: Exception) { "" }
}
