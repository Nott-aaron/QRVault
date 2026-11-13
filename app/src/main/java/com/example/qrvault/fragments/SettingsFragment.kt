package com.example.qrvault.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.qrvault.database.QRDatabaseHelper
import com.example.qrvault.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: QRDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = QRDatabaseHelper(requireContext())

        binding.cardClearHistory.setOnClickListener {
            showClearAllConfirmationDialog()
        }
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All QR History")
            .setMessage("Are you sure you want to delete all saved QR history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                val rows = dbHelper.deleteAllHistory()
                Toast.makeText(requireContext(), "$rows history items deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}