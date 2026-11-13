package com.example.qrvault.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrvault.activities.MainActivity
import com.example.qrvault.activities.ScannerActivity
import com.example.qrvault.database.QRDatabaseHelper
import com.example.qrvault.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: QRDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = QRDatabaseHelper(requireContext())

        setupClickListeners()
        loadStatistics()
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }

    private fun setupClickListeners() {
        binding.cardContacts.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(MainActivity.TAB_CONTACTS)
        }

        binding.cardGenerate.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(MainActivity.TAB_CONTACTS)
        }

        binding.cardScan.setOnClickListener {
            val intent = Intent(requireContext(), ScannerActivity::class.java)
            startActivity(intent)
        }

        binding.cardHistory.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(MainActivity.TAB_HISTORY)
        }
    }

    private fun loadStatistics() {
        val stats = dbHelper.getStats()
        binding.txtTotalGenerated.text = stats.totalGenerated.toString()
        binding.txtTotalSaved.text = stats.totalSaved.toString()
        binding.txtRecentContact.text = stats.recentContactName ?: "None yet"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}