package com.example.qrvault.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.qrvault.databinding.ItemContactBinding
import com.example.qrvault.models.ContactModel

class ContactAdapter(
    private var contacts: List<ContactModel>,
    private val onContactClick: (ContactModel) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun updateData(newContacts: List<ContactModel>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ContactModel) {
            binding.txtName.text = contact.name

            if (contact.phoneNumber.isNotBlank()) {
                binding.txtPhone.visibility = View.VISIBLE
                binding.txtPhone.text = contact.phoneNumber
            } else {
                binding.txtPhone.visibility = View.GONE
            }

            if (contact.email.isNotBlank()) {
                binding.txtEmail.visibility = View.VISIBLE
                binding.txtEmail.text = contact.email
            } else {
                binding.txtEmail.visibility = View.GONE
            }

            // Avatar initial
            val initial = if (contact.name.isNotBlank()) contact.name.trim().take(1).uppercase() else "?"
            binding.txtInitial.text = initial

            if (contact.avatarColor != 0) {
                val color = ContextCompat.getColor(binding.root.context, contact.avatarColor)
                binding.layoutAvatar.background.setTint(color)
            }

            binding.root.setOnClickListener {
                onContactClick(contact)
            }
        }
    }
}