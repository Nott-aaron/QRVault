package com.example.qrvault.activities

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrvault.database.QRDatabaseHelper
import com.example.qrvault.databinding.ActivityQrGeneratorBinding
import com.example.qrvault.models.ContactModel
import com.example.qrvault.utils.QRCodeGenerator
import com.example.qrvault.utils.QRStorageUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QRGeneratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrGeneratorBinding
    private var contact: ContactModel? = null
    private var qrBitmap: Bitmap? = null
    private var vCardContent: String = ""
    private lateinit var dbHelper: QRDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = QRDatabaseHelper(this)

        contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("CONTACT_EXTRA", ContactModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("CONTACT_EXTRA") as? ContactModel
        }

        if (contact == null) {
            Toast.makeText(this, "No contact details provided.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupContactInfoDisplay()
        generateAndDisplayQR()
        saveToDatabaseHistory()
        setupListeners()
    }

    private fun setupContactInfoDisplay() {
        val c = contact ?: return
        binding.txtName.text = c.name
        binding.txtPhone.text = if (c.phoneNumber.isNotBlank()) c.phoneNumber else "No phone"

        if (c.email.isNotBlank()) {
            binding.txtEmail.visibility = View.VISIBLE
            binding.txtEmail.text = c.email
        } else {
            binding.txtEmail.visibility = View.GONE
        }
    }

    private fun generateAndDisplayQR() {
        val c = contact ?: return
        vCardContent = QRCodeGenerator.createVCard(c.name, c.phoneNumber, c.email)
        qrBitmap = QRCodeGenerator.generateQRCodeBitmap(vCardContent, 512, 512)

        if (qrBitmap != null) {
            binding.imgQRCode.setImageBitmap(qrBitmap)
        } else {
            Toast.makeText(this, "Failed to generate QR Code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDatabaseHistory() {
        val c = contact ?: return
        if (vCardContent.isBlank()) return

        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        dbHelper.insertQRHistory(
            contactName = c.name,
            phoneNumber = c.phoneNumber,
            email = c.email,
            qrContent = vCardContent,
            createdAt = currentDate
        )
    }

    private fun setupListeners() {
        binding.btnHeaderBack.setOnClickListener {
            finish()
        }

        binding.btnBackToContacts.setOnClickListener {
            finish()
        }

        binding.btnSaveQR.setOnClickListener {
            val bitmap = qrBitmap
            val name = contact?.name ?: "Contact"
            if (bitmap != null) {
                val savedUri = QRStorageUtils.saveQRCodeToGallery(this, bitmap, name)
                if (savedUri != null) {
                    Toast.makeText(this, "QR Code saved to gallery successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save QR Code to gallery.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnShareQR.setOnClickListener {
            val bitmap = qrBitmap
            val name = contact?.name ?: "Contact"
            if (bitmap != null) {
                QRStorageUtils.shareQRCode(this, bitmap, name)
            }
        }

        binding.btnRegenerateQR.setOnClickListener {
            generateAndDisplayQR()
            saveToDatabaseHistory()
            Toast.makeText(this, "QR Code regenerated!", Toast.LENGTH_SHORT).show()
        }
    }
}