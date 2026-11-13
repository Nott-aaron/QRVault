package com.example.qrvault.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrvault.activities.QRGeneratorActivity
import com.example.qrvault.adapters.HistoryAdapter
import com.example.qrvault.database.QRDatabaseHelper
import com.example.qrvault.databinding.FragmentHistoryBinding
import com.example.qrvault.models.ContactModel
import com.example.qrvault.models.QRHistoryModel
import com.example.qrvault.utils.QRCodeGenerator
import com.example.qrvault.utils.QRStorageUtils

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: QRDatabaseHelper
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = QRDatabaseHelper(requireContext())

        setupRecyclerView()

        binding.btnClearAll.setOnClickListener {
            showClearAllConfirmationDialog()
        }

        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            historyList = emptyList(),
            onItemClick = { historyItem ->
                val contact = ContactModel(
                    id = historyItem.id.toString(),
                    name = historyItem.contactName,
                    phoneNumber = historyItem.phoneNumber,
                    email = historyItem.email
                )
                val intent = Intent(requireContext(), QRGeneratorActivity::class.java).apply {
                    putExtra("CONTACT_EXTRA", contact)
                }
                startActivity(intent)
            },
            onShareClick = { historyItem ->
                val bitmap = QRCodeGenerator.generateQRCodeBitmap(historyItem.qrContent, 512, 512)
                if (bitmap != null) {
                    QRStorageUtils.shareQRCode(requireContext(), bitmap, historyItem.contactName)
                } else {
                    Toast.makeText(requireContext(), "Failed to generate QR Code bitmap.", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { historyItem ->
                showDeleteSingleConfirmationDialog(historyItem)
            }
        )

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = historyAdapter
    }

    private fun loadHistory() {
        val list = dbHelper.getAllHistory()
        historyAdapter.updateData(list)

        if (list.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
            binding.btnClearAll.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            binding.btnClearAll.visibility = View.VISIBLE
        }
    }

    private fun showDeleteSingleConfirmationDialog(item: QRHistoryModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete History Record")
            .setMessage("Are you sure you want to delete the QR history for ${item.contactName}?")
            .setPositiveButton("Delete") { _, _ ->
                val rows = dbHelper.deleteHistory(item.id)
                if (rows > 0) {
                    Toast.makeText(requireContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show()
                    loadHistory()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All QR History")
            .setMessage("Are you sure you want to delete all saved QR history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                val rows = dbHelper.deleteAllHistory()
                Toast.makeText(requireContext(), "$rows history items deleted", Toast.LENGTH_SHORT).show()
                loadHistory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}