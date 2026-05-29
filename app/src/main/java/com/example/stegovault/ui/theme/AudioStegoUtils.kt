package com.example.stegovault.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class WavInfo(
    val dataOffset: Int,
    val dataSize: Int,
    val numChannels: Int,
    val sampleRate: Int,
    val bitsPerSample: Int
)

private fun readIntLE(bytes: ByteArray, off: Int): Int =
    ByteBuffer.wrap(bytes, off, 4).order(ByteOrder.LITTLE_ENDIAN).int

private fun readShortLE(bytes: ByteArray, off: Int): Short =
    ByteBuffer.wrap(bytes, off, 2).order(ByteOrder.LITTLE_ENDIAN).short

private fun writeShortLE(bytes: ByteArray, off: Int, value: Short) {
    val bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value)
    bytes[off] = bb.array()[0]
    bytes[off + 1] = bb.array()[1]
}

fun parseWav(bytes: ByteArray): WavInfo {
    if (bytes.size < 44) throw IllegalArgumentException("Invalid WAV: too small")
    val riff = String(bytes, 0, 4)
    val wave = String(bytes, 8, 4)
    if (riff != "RIFF" || wave != "WAVE") throw IllegalArgumentException("Not a WAV file")

    var fmtFound = false
    var dataFound = false
    var numChannels = 0
    var sampleRate = 0
    var bitsPerSample = 0
    var dataOffset = -1
    var dataSize = -1

    var i = 12
    while (i + 8 <= bytes.size) {
        val chunkId = String(bytes, i, 4)
        val chunkSize = readIntLE(bytes, i + 4)
        val chunkDataStart = i + 8

        if (chunkDataStart + chunkSize > bytes.size) break

        when (chunkId) {
            "fmt " -> {
                fmtFound = true
                val audioFormat = readShortLE(bytes, chunkDataStart).toInt() and 0xFFFF
                if (audioFormat != 1) throw IllegalArgumentException("WAV must be PCM (audioFormat=1)")
                numChannels = readShortLE(bytes, chunkDataStart + 2).toInt() and 0xFFFF
                sampleRate = readIntLE(bytes, chunkDataStart + 4)
                bitsPerSample = readShortLE(bytes, chunkDataStart + 14).toInt() and 0xFFFF
            }
            "data" -> {
                dataFound = true
                dataOffset = chunkDataStart
                dataSize = chunkSize
            }
        }

        i = chunkDataStart + chunkSize
        // chunks are word-aligned
        if (i % 2 == 1) i++
        if (fmtFound && dataFound) break
    }

    if (!fmtFound || !dataFound) throw IllegalArgumentException("Invalid WAV: missing fmt/data chunk")
    if (bitsPerSample != 16) throw IllegalArgumentException("Only 16-bit PCM WAV supported")

    return WavInfo(
        dataOffset = dataOffset,
        dataSize = dataSize,
        numChannels = numChannels,
        sampleRate = sampleRate,
        bitsPerSample = bitsPerSample
    )
}

/**
 * Embed message using LSB into 16-bit samples.
 * Payload format: [4-byte length little endian] + [message bytes]
 */
fun encodeWavLSB(wavBytes: ByteArray, message: String): ByteArray {
    val info = parseWav(wavBytes)
    val out = wavBytes.copyOf()

    val msgBytes = message.toByteArray(Charsets.UTF_8)
    val lenBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(msgBytes.size).array()
    val payload = lenBytes + msgBytes

    val requiredBits = payload.size * 8
    val sampleCount = info.dataSize / 2 // 16-bit samples
    if (requiredBits > sampleCount) {
        throw IllegalArgumentException("Message too long. Capacity: ${sampleCount / 8} bytes")
    }

    var bitPos = 0
    var dataPtr = info.dataOffset

    fun getBit(b: Byte, bitIndexFromMSB: Int): Int {
        // bitIndexFromMSB: 0..7 (MSB->LSB)
        val v = b.toInt() and 0xFF
        return (v shr (7 - bitIndexFromMSB)) and 1
    }

    for (p in payload) {
        for (k in 0 until 8) {
            val bit = getBit(p, k)
            val sample = readShortLE(out, dataPtr)
            val newSample = ((sample.toInt() and 0xFFFE) or bit).toShort()
            writeShortLE(out, dataPtr, newSample)
            dataPtr += 2
            bitPos++
        }
    }

    return out
}
fun decodeWavLSB(wavBytes: ByteArray): String {
    val info = parseWav(wavBytes)
    val dataStart = info.dataOffset

    fun readBit(sampleIndex: Int): Int {
        val off = dataStart + sampleIndex * 2
        val sample = readShortLE(wavBytes, off).toInt()
        return sample and 1
    }

    // Read one byte from LSB bits (8 samples), MSB-first inside that byte (same as encode)
    fun readByte(bitOffset: Int): Byte {
        var v = 0
        for (k in 0 until 8) {
            v = (v shl 1) or readBit(bitOffset + k)
        }
        return v.toByte()
    }

    // 1) Read 4 length bytes (these were stored little-endian)
    val lenBytes = ByteArray(4)
    for (i in 0 until 4) {
        lenBytes[i] = readByte(i * 8)
    }

    val len = ByteBuffer.wrap(lenBytes)
        .order(ByteOrder.LITTLE_ENDIAN)
        .int

    if (len <= 0) return ""

    // Safety check
    val sampleCount = info.dataSize / 2
    val totalBitsNeeded = 32 + len * 8
    if (totalBitsNeeded > sampleCount) return ""

    // 2) Read message bytes
    val msg = ByteArray(len)
    var bitIndex = 32
    for (i in 0 until len) {
        msg[i] = readByte(bitIndex)
        bitIndex += 8
    }

    return msg.toString(Charsets.UTF_8)
}
