package com.example.qrvault.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrvault.databinding.ItemHistoryBinding
import com.example.qrvault.models.QRHistoryModel
import com.example.qrvault.utils.QRCodeGenerator

class HistoryAdapter(
    private var historyList: List<QRHistoryModel>,
    private val onItemClick: (QRHistoryModel) -> Unit,
    private val onShareClick: (QRHistoryModel) -> Unit,
    private val onDeleteClick: (QRHistoryModel) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    fun updateData(newHistory: List<QRHistoryModel>) {
        historyList = newHistory
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QRHistoryModel) {
            binding.txtName.text = item.contactName

            if (item.phoneNumber.isNotBlank()) {
                binding.txtPhone.visibility = View.VISIBLE
                binding.txtPhone.text = item.phoneNumber
            } else {
                binding.txtPhone.visibility = View.GONE
            }

            binding.txtDate.text = item.createdAt

            // Generate strict black-and-white QR thumbnail bitmap
            val qrBitmap = QRCodeGenerator.generateQRCodeBitmap(item.qrContent, 120, 120)
            if (qrBitmap != null) {
                binding.imgQRThumbnail.setImageBitmap(qrBitmap)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.btnShare.setOnClickListener {
                onShareClick(item)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}