package com.example.qrvault.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.qrvault.R
import com.example.qrvault.models.ContactModel

object ContactRepository {

    private val avatarColors = intArrayOf(
        R.color.avatar_pink_1,
        R.color.avatar_pink_2,
        R.color.avatar_pink_3,
        R.color.avatar_purple,
        R.color.avatar_teal
    )

    fun getContacts(context: Context, searchQuery: String? = null): List<ContactModel> {
        val contactsMap = LinkedHashMap<String, MutableContact>()
        val contentResolver = context.contentResolver

        // 1. Query Phone numbers
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (!searchQuery.isNullOrBlank()) {
            selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
            selectionArgs = arrayOf("%${searchQuery.trim()}%")
        }

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        try {
            contentResolver.query(
                phoneUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor.moveToNext()) {
                    val id = if (idIdx != -1) cursor.getString(idIdx) else ""
                    val name = if (nameIdx != -1) cursor.getString(nameIdx) else ""
                    val number = if (numberIdx != -1) cursor.getString(numberIdx) else ""

                    if (id.isNotBlank() && name.isNotBlank()) {
                        if (!contactsMap.containsKey(id)) {
                            contactsMap[id] = MutableContact(
                                id = id,
                                name = name,
                                phoneNumber = number
                            )
                        } else if (contactsMap[id]?.phoneNumber.isNullOrBlank()) {
                            contactsMap[id]?.phoneNumber = number
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Query Emails for retrieved contacts
        if (contactsMap.isNotEmpty()) {
            val emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI
            val emailProjection = arrayOf(
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS
            )

            try {
                contentResolver.query(
                    emailUri,
                    emailProjection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
                    val emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

                    while (cursor.moveToNext()) {
                        val id = if (idIdx != -1) cursor.getString(idIdx) else ""
                        val email = if (emailIdx != -1) cursor.getString(emailIdx) else ""

                        if (id.isNotBlank() && email.isNotBlank()) {
                            contactsMap[id]?.let { contact ->
                                if (contact.email.isBlank()) {
                                    contact.email = email
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Map to ContactModel list with assigned avatar colors
        var colorIdx = 0
        return contactsMap.values.map { mutable ->
            val color = avatarColors[colorIdx % avatarColors.size]
            colorIdx++
            ContactModel(
                id = mutable.id,
                name = mutable.name,
                phoneNumber = mutable.phoneNumber,
                email = mutable.email,
                avatarColor = color
            )
        }
    }

    private data class MutableContact(
        val id: String,
        val name: String,
        var phoneNumber: String,
        var email: String = ""
    )
}