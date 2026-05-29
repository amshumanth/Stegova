package com.example.stegovault.utils

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private fun generateKey(password: String): SecretKeySpec {
    val sha = MessageDigest.getInstance("SHA-256")
    val key = sha.digest(password.toByteArray(Charsets.UTF_8))
    return SecretKeySpec(key, "AES")
}

fun encryptText(plainText: String, password: String): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, generateKey(password))
    val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encrypted, Base64.NO_WRAP)
}

/** returns "" if wrong key / invalid data */
fun decryptText(encryptedText: String, password: String): String {
    return try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, generateKey(password))
        val decoded = Base64.decode(encryptedText, Base64.NO_WRAP)
        String(cipher.doFinal(decoded), Charsets.UTF_8)
    } catch (e: Exception) {
        ""
    }
}
