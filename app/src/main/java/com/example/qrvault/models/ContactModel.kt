package com.example.qrvault.models

import java.io.Serializable

data class ContactModel(
    val id: String = "",
    val name: String,
    val phoneNumber: String = "",
    val email: String = "",
    val avatarColor: Int = 0
) : Serializable