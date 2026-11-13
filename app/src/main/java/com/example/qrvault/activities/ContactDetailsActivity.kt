package com.example.qrvault.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qrvault.databinding.ActivityContactDetailsBinding
import com.example.qrvault.models.ContactModel

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailsBinding
    private var contact: ContactModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contact = intent.getSerializableExtra("CONTACT_EXTRA") as? ContactModel

        if (contact == null) {
            finish()
            return
        }

        displayContactDetails()
        setupListeners()
    }

    private fun displayContactDetails() {
        val currentContact = contact ?: return

        binding.txtName.text = currentContact.name
        binding.txtPhone.text = if (currentContact.phoneNumber.isNotBlank()) currentContact.phoneNumber else "No phone number available"

        if (currentContact.email.isNotBlank()) {
            binding.cardEmail.visibility = View.VISIBLE
            binding.txtEmail.text = currentContact.email
        } else {
            binding.cardEmail.visibility = View.GONE
        }

        val initial = if (currentContact.name.isNotBlank()) currentContact.name.trim().take(1).uppercase() else "?"
        binding.txtInitial.text = initial

        if (currentContact.avatarColor != 0) {
            val color = ContextCompat.getColor(this, currentContact.avatarColor)
            binding.layoutAvatar.background.setTint(color)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnGenerateQR.setOnClickListener {
            val currentContact = contact ?: return@setOnClickListener
            val intent = Intent(this, QRGeneratorActivity::class.java).apply {
                putExtra("CONTACT_EXTRA", currentContact)
            }
            startActivity(intent)
        }
    }
}