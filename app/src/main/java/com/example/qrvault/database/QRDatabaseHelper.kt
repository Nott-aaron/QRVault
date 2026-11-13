package com.example.qrvault.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.qrvault.models.QRHistoryModel

data class HistoryStats(
    val totalGenerated: Int,
    val totalSaved: Int,
    val recentContactName: String?
)

class QRDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "qrvault.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "qr_history"
        const val COLUMN_ID = "id"
        const val COLUMN_CONTACT_NAME = "contact_name"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_QR_CONTENT = "qr_content"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CONTACT_NAME TEXT NOT NULL,
                $COLUMN_PHONE_NUMBER TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_QR_CONTENT TEXT NOT NULL,
                $COLUMN_CREATED_AT TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertQRHistory(
        contactName: String,
        phoneNumber: String,
        email: String,
        qrContent: String,
        createdAt: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTACT_NAME, contactName)
            put(COLUMN_PHONE_NUMBER, phoneNumber)
            put(COLUMN_EMAIL, email)
            put(COLUMN_QR_CONTENT, qrContent)
            put(COLUMN_CREATED_AT, createdAt)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllHistory(): List<QRHistoryModel> {
        val historyList = mutableListOf<QRHistoryModel>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)) ?: ""
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: ""
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QR_CONTENT))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))

                historyList.add(
                    QRHistoryModel(
                        id = id,
                        contactName = name,
                        phoneNumber = phone,
                        email = email,
                        qrContent = content,
                        createdAt = createdAt
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }

    fun getHistoryById(id: Long): QRHistoryModel? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var item: QRHistoryModel? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)) ?: ""
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: ""
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QR_CONTENT))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))

            item = QRHistoryModel(
                id = id,
                contactName = name,
                phoneNumber = phone,
                email = email,
                qrContent = content,
                createdAt = createdAt
            )
        }
        cursor.close()
        db.close()
        return item
    }

    fun deleteHistory(id: Long): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    fun deleteAllHistory(): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_NAME, null, null)
        db.close()
        return rowsDeleted
    }

    fun getStats(): HistoryStats {
        val db = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_NAME"
        val cursor = db.rawQuery(countQuery, null)
        var totalCount = 0
        if (cursor.moveToFirst()) {
            totalCount = cursor.getInt(0)
        }
        cursor.close()

        val recentQuery = "SELECT $COLUMN_CONTACT_NAME FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC LIMIT 1"
        val recentCursor = db.rawQuery(recentQuery, null)
        var recentName: String? = null
        if (recentCursor.moveToFirst()) {
            recentName = recentCursor.getString(0)
        }
        recentCursor.close()
        db.close()

        return HistoryStats(
            totalGenerated = totalCount,
            totalSaved = totalCount,
            recentContactName = recentName
        )
    }
}