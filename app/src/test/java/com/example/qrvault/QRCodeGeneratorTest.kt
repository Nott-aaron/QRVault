package com.example.qrvault

import com.example.qrvault.utils.QRCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QRCodeGeneratorTest {

    @Test
    fun testCreateVCard() {
        val vCard = QRCodeGenerator.createVCard(
            name = "Spencer Fernandes",
            phone = "9876543210",
            email = "spencer@example.com"
        )

        assertTrue(vCard.contains("BEGIN:VCARD"))
        assertTrue(vCard.contains("VERSION:3.0"))
        assertTrue(vCard.contains("FN:Spencer Fernandes"))
        assertTrue(vCard.contains("TEL:9876543210"))
        assertTrue(vCard.contains("EMAIL:spencer@example.com"))
        assertTrue(vCard.contains("END:VCARD"))
    }

    @Test
    fun testCreateVCardWithMissingFields() {
        val vCard = QRCodeGenerator.createVCard(
            name = "John Doe",
            phone = "",
            email = ""
        )

        assertTrue(vCard.contains("BEGIN:VCARD"))
        assertTrue(vCard.contains("FN:John Doe"))
        assertTrue(!vCard.contains("TEL:"))
        assertTrue(!vCard.contains("EMAIL:"))
        assertTrue(vCard.contains("END:VCARD"))
    }

    @Test
    fun testParseVCard() {
        val vCardText = """
            BEGIN:VCARD
            VERSION:3.0
            FN:Jane Doe
            TEL:1234567890
            EMAIL:jane@example.com
            END:VCARD
        """.trimIndent()

        val contact = QRCodeGenerator.parseVCard(vCardText)

        assertEquals("Jane Doe", contact.name)
        assertEquals("1234567890", contact.phoneNumber)
        assertEquals("jane@example.com", contact.email)
    }
}