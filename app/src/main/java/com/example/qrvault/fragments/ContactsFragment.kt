package com.example.qrvault.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrvault.activities.ContactDetailsActivity
import com.example.qrvault.adapters.ContactAdapter
import com.example.qrvault.databinding.FragmentContactsBinding
import com.example.qrvault.models.ContactModel
import com.example.qrvault.utils.ContactRepository

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactAdapter: ContactAdapter
    private var allContacts: List<ContactModel> = emptyList()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.cardPermission.visibility = View.GONE
            loadContacts()
        } else {
            binding.cardPermission.visibility = View.VISIBLE
            binding.rvContacts.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "Contacts permission is required to select contacts for QR code generation.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()

        binding.btnGrantPermission.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }

        checkPermissionAndLoadContacts()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        }
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            val intent = Intent(requireContext(), ContactDetailsActivity::class.java).apply {
                putExtra("CONTACT_EXTRA", contact)
            }
            startActivity(intent)
        }
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = contactAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s?.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkPermissionAndLoadContacts() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.cardPermission.visibility = View.GONE
                loadContacts()
            }
            else -> {
                binding.cardPermission.visibility = View.VISIBLE
                binding.rvContacts.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.GONE
            }
        }
    }

    private fun loadContacts() {
        Thread {
            val contacts = ContactRepository.getContacts(requireContext())
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                allContacts = contacts
                binding.cardPermission.visibility = View.GONE
                filterContacts(binding.etSearch.text?.toString())
            }
        }.start()
    }

    private fun filterContacts(query: String?) {
        val filtered = if (query.isNullOrBlank()) {
            allContacts
        } else {
            allContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phoneNumber.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
        }

        contactAdapter.updateData(filtered)

        if (filtered.isEmpty() && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvContacts.visibility = View.GONE
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvContacts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}