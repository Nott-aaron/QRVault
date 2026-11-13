package com.example.qrvault.models

import java.io.Serializable

data class QRHistoryModel(
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String = "",
    val email: String = "",
    val qrContent: String,
    val createdAt: String
) : Serializable