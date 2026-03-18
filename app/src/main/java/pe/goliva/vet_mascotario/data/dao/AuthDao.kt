package pe.goliva.vet_mascotario.data.dao

import android.content.ContentValues
import android.content.Context
import pe.goliva.vet_mascotario.data.db.DatabaseContract
import pe.goliva.vet_mascotario.data.db.DatabaseHelper
import pe.goliva.vet_mascotario.data.model.AuthUser

class AuthDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun authenticate(email: String, password: String): AuthUser? {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 
                ${DatabaseContract.UserTable.COL_USER_ID},
                ${DatabaseContract.UserTable.COL_OWNER_ID},
                ${DatabaseContract.UserTable.COL_FULL_NAME},
                ${DatabaseContract.UserTable.COL_EMAIL},
                ${DatabaseContract.UserTable.COL_PHONE},
                ${DatabaseContract.UserTable.COL_HOME_BRANCH_ID}
            FROM ${DatabaseContract.UserTable.TABLE_NAME}
            WHERE ${DatabaseContract.UserTable.COL_EMAIL} = ?
              AND ${DatabaseContract.UserTable.COL_PASSWORD_HASH} = ?
              AND ${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(email, password))
        cursor.use {
            if (it.moveToFirst()) {
                return AuthUser(
                    userId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_USER_ID)),
                    ownerId = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_OWNER_ID))) {
                        null
                    } else {
                        it.getLong(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_OWNER_ID))
                    },
                    fullName = it.getString(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_FULL_NAME)),
                    email = it.getString(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_EMAIL)),
                    phone = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_PHONE))) {
                        null
                    } else {
                        it.getString(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_PHONE))
                    },
                    homeBranchId = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_HOME_BRANCH_ID))) {
                        null
                    } else {
                        it.getLong(it.getColumnIndexOrThrow(DatabaseContract.UserTable.COL_HOME_BRANCH_ID))
                    }
                )
            }
        }
        return null
    }

    fun emailExists(email: String): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 1
            FROM ${DatabaseContract.UserTable.TABLE_NAME}
            WHERE ${DatabaseContract.UserTable.COL_EMAIL} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(email))
        cursor.use {
            return it.moveToFirst()
        }
    }

    fun verifyCurrentPassword(userId: Long, currentPassword: String): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 1
            FROM ${DatabaseContract.UserTable.TABLE_NAME}
            WHERE ${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND ${DatabaseContract.UserTable.COL_PASSWORD_HASH} = ?
              AND ${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), currentPassword))
        cursor.use {
            return it.moveToFirst()
        }
    }

    fun updatePassword(userId: Long, newPassword: String): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseContract.UserTable.COL_PASSWORD_HASH, newPassword)
            }

            val updatedRows = db.update(
                DatabaseContract.UserTable.TABLE_NAME,
                values,
                "${DatabaseContract.UserTable.COL_USER_ID} = ? AND ${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1",
                arrayOf(userId.toString())
            )

            updatedRows > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun registerClientUser(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        homeBranchId: Long = 1L
    ): Boolean {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        return try {
            val ownerValues = ContentValues().apply {
                put(DatabaseContract.OwnerTable.COL_DOC_TYPE, "DNI")
                put(DatabaseContract.OwnerTable.COL_DOC_NUMBER, "")
                put(DatabaseContract.OwnerTable.COL_FULL_NAME, fullName)
                put(DatabaseContract.OwnerTable.COL_PHONE, phone)
                put(DatabaseContract.OwnerTable.COL_EMAIL, email)
                put(DatabaseContract.OwnerTable.COL_ADDRESS, "")
                put(DatabaseContract.OwnerTable.COL_IS_ACTIVE, 1)
                put(DatabaseContract.OwnerTable.COL_CREATED_AT, nowUtc())
            }
            val ownerId = db.insert(DatabaseContract.OwnerTable.TABLE_NAME, null, ownerValues)

            if (ownerId == -1L) {
                throw Exception("Error al registrar Owner")
            }

            val userValues = ContentValues().apply {
                put(DatabaseContract.UserTable.COL_HOME_BRANCH_ID, homeBranchId)
                put(DatabaseContract.UserTable.COL_OWNER_ID, ownerId)
                put(DatabaseContract.UserTable.COL_FULL_NAME, fullName)
                put(DatabaseContract.UserTable.COL_EMAIL, email)
                put(DatabaseContract.UserTable.COL_PHONE, phone)
                put(DatabaseContract.UserTable.COL_PASSWORD_HASH, password)
                put(DatabaseContract.UserTable.COL_IS_ACTIVE, 1)
                put(DatabaseContract.UserTable.COL_CREATED_AT, nowUtc())
                put(DatabaseContract.UserTable.COL_LAST_LOGIN_AT, "")
            }

            val userId = db.insert(DatabaseContract.UserTable.TABLE_NAME, null, userValues)

            if (userId == -1L) {
                throw Exception("No se pudo registrar el usuario")
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    private fun nowUtc(): String {
        return java.time.LocalDateTime.now().toString()
    }
}