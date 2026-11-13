package com.example.qrvault.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrvault.databinding.ActivityScannedResultBinding
import com.example.qrvault.models.ContactModel

class ScannedResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannedResultBinding
    private var scannedContact: ContactModel? = null
    private var rawContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannedResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannedContact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SCANNED_CONTACT_EXTRA", ContactModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("SCANNED_CONTACT_EXTRA") as? ContactModel
        }
        rawContent = intent.getStringExtra("RAW_CONTENT_EXTRA") ?: ""

        if (scannedContact == null) {
            Toast.makeText(this, "No scanned details available.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayScannedInfo()
        setupListeners()
    }

    private fun displayScannedInfo() {
        val contact = scannedContact ?: return

        binding.txtName.text = contact.name
        binding.txtPhone.text = if (contact.phoneNumber.isNotBlank()) contact.phoneNumber else "No phone number"

        if (contact.email.isNotBlank()) {
            binding.txtEmail.visibility = View.VISIBLE
            binding.txtEmail.text = contact.email
        } else {
            binding.txtEmail.visibility = View.GONE
        }

        val initial = if (contact.name.isNotBlank()) contact.name.trim().take(1).uppercase() else "?"
        binding.txtInitial.text = initial

        binding.txtRawContent.text = if (rawContent.isNotBlank()) rawContent else "No raw payload"
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            finish()
        }

        binding.btnAddContact.setOnClickListener {
            val contact = scannedContact ?: return@setOnClickListener
            try {
                val insertIntent = Intent(Intent.ACTION_INSERT).apply {
                    type = ContactsContract.Contacts.CONTENT_TYPE
                    putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
                    if (contact.phoneNumber.isNotBlank()) {
                        putExtra(ContactsContract.Intents.Insert.PHONE, contact.phoneNumber)
                    }
                    if (contact.email.isNotBlank()) {
                        putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
                    }
                }
                startActivity(insertIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Unable to open device contacts app.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGenerateQR.setOnClickListener {
            val contact = scannedContact ?: return@setOnClickListener
            val intent = Intent(this, QRGeneratorActivity::class.java).apply {
                putExtra("CONTACT_EXTRA", contact)
            }
            startActivity(intent)
        }
    }
}