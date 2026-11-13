package com.example.qrvault.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.example.qrvault.models.ContactModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.util.EnumMap

object QRCodeGenerator {

    /**
     * Converts contact details into standard vCard format 3.0
     */
    fun createVCard(name: String, phone: String, email: String): String {
        val sb = StringBuilder()
        sb.append("BEGIN:VCARD\n")
        sb.append("VERSION:3.0\n")
        sb.append("FN:").append(name.trim()).append("\n")
        if (phone.isNotBlank()) {
            sb.append("TEL:").append(phone.trim()).append("\n")
        }
        if (email.isNotBlank()) {
            sb.append("EMAIL:").append(email.trim()).append("\n")
        }
        sb.append("END:VCARD")
        return sb.toString()
    }

    /**
     * Generates a QR Code bitmap.
     * STRICT REQUIREMENT: Generated QR codes MUST ALWAYS use a pure black foreground
     * and pure white background.
     */
    fun generateQRCodeBitmap(content: String, width: Int = 512, height: Int = 512): Bitmap? {
        if (content.isEmpty()) return null

        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 2)
            }

            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )

            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val pixels = IntArray(matrixWidth * matrixHeight)

            // STRICT BLACK AND WHITE COLOURS ONLY
            val pureBlack = Color.BLACK // 0xFF000000
            val pureWhite = Color.WHITE // 0xFFFFFFFF

            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) pureBlack else pureWhite
                }
            }

            Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parses scanned vCard content or plain text into a ContactModel.
     */
    fun parseVCard(content: String): ContactModel {
        var name = ""
        var phone = ""
        var email = ""

        if (content.contains("BEGIN:VCARD", ignoreCase = true)) {
            val lines = content.split("\n", "\r")
            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("FN:", ignoreCase = true) -> {
                        name = trimmed.substring(3).trim()
                    }
                    trimmed.startsWith("N:", ignoreCase = true) && name.isEmpty() -> {
                        name = trimmed.substring(2).replace(";", " ").trim()
                    }
                    trimmed.startsWith("TEL", ignoreCase = true) -> {
                        val colonIdx = trimmed.indexOf(":")
                        if (colonIdx != -1) {
                            phone = trimmed.substring(colonIdx + 1).trim()
                        }
                    }
                    trimmed.startsWith("EMAIL", ignoreCase = true) -> {
                        val colonIdx = trimmed.indexOf(":")
                        if (colonIdx != -1) {
                            email = trimmed.substring(colonIdx + 1).trim()
                        }
                    }
                }
            }
        } else {
            // Fallback for plain text format
            name = content.take(30).trim()
        }

        if (name.isEmpty()) name = "Scanned Contact"

        return ContactModel(
            id = "",
            name = name,
            phoneNumber = phone,
            email = email
        )
    }
}