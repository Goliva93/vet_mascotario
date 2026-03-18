package pe.goliva.vet_mascotario.data.dao

import android.content.ContentValues
import android.content.Context
import pe.goliva.vet_mascotario.data.db.DatabaseContract
import pe.goliva.vet_mascotario.data.db.DatabaseHelper
import pe.goliva.vet_mascotario.data.model.BranchOption
import pe.goliva.vet_mascotario.data.model.UserProfile

class UserProfileDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getUserProfileById(userId: Long): UserProfile? {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 
                u.${DatabaseContract.UserTable.COL_USER_ID},
                u.${DatabaseContract.UserTable.COL_OWNER_ID},
                u.${DatabaseContract.UserTable.COL_FULL_NAME},
                u.${DatabaseContract.UserTable.COL_EMAIL},
                u.${DatabaseContract.UserTable.COL_PHONE},
                u.${DatabaseContract.UserTable.COL_HOME_BRANCH_ID},
                b.${DatabaseContract.BranchTable.COL_NAME} AS BranchName
            FROM ${DatabaseContract.UserTable.TABLE_NAME} u
            LEFT JOIN ${DatabaseContract.BranchTable.TABLE_NAME} b
                ON u.${DatabaseContract.UserTable.COL_HOME_BRANCH_ID} = b.${DatabaseContract.BranchTable.COL_BRANCH_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                return UserProfile(
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
                    },
                    homeBranchName = if (it.isNull(it.getColumnIndexOrThrow("BranchName"))) {
                        null
                    } else {
                        it.getString(it.getColumnIndexOrThrow("BranchName"))
                    }
                )
            }
        }
        return null
    }

    fun getActiveBranches(): List<BranchOption> {
        val db = dbHelper.readableDatabase
        val branches = mutableListOf<BranchOption>()

        val query = """
            SELECT
                ${DatabaseContract.BranchTable.COL_BRANCH_ID},
                ${DatabaseContract.BranchTable.COL_NAME},
                ${DatabaseContract.BranchTable.COL_ADDRESS},
                ${DatabaseContract.BranchTable.COL_PHONE}
            FROM ${DatabaseContract.BranchTable.TABLE_NAME}
            WHERE ${DatabaseContract.BranchTable.COL_IS_ACTIVE} = 1
            ORDER BY ${DatabaseContract.BranchTable.COL_BRANCH_ID} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                branches.add(
                    BranchOption(
                        branchId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_BRANCH_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_NAME)),
                        address = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_ADDRESS))) {
                            null
                        } else {
                            it.getString(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_ADDRESS))
                        },
                        phone = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_PHONE))) {
                            null
                        } else {
                            it.getString(it.getColumnIndexOrThrow(DatabaseContract.BranchTable.COL_PHONE))
                        }
                    )
                )
            }
        }

        return branches
    }

    fun emailExistsForAnotherUser(email: String, userId: Long): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 1
            FROM ${DatabaseContract.UserTable.TABLE_NAME}
            WHERE ${DatabaseContract.UserTable.COL_EMAIL} = ?
              AND ${DatabaseContract.UserTable.COL_USER_ID} <> ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(email, userId.toString()))

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun updateUserProfile(
        userId: Long,
        fullName: String,
        email: String,
        phone: String?
    ): Boolean {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        return try {
            var ownerId: Long? = null

            val ownerCursor = db.rawQuery(
                """
                SELECT ${DatabaseContract.UserTable.COL_OWNER_ID}
                FROM ${DatabaseContract.UserTable.TABLE_NAME}
                WHERE ${DatabaseContract.UserTable.COL_USER_ID} = ?
                LIMIT 1
                """.trimIndent(),
                arrayOf(userId.toString())
            )

            ownerCursor.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    ownerId = it.getLong(0)
                }
            }

            val userValues = ContentValues().apply {
                put(DatabaseContract.UserTable.COL_FULL_NAME, fullName)
                put(DatabaseContract.UserTable.COL_EMAIL, email)
                put(DatabaseContract.UserTable.COL_PHONE, phone)
            }

            val updatedUserRows = db.update(
                DatabaseContract.UserTable.TABLE_NAME,
                userValues,
                "${DatabaseContract.UserTable.COL_USER_ID} = ? AND ${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1",
                arrayOf(userId.toString())
            )

            if (updatedUserRows <= 0) {
                throw IllegalStateException("No se pudo actualizar el usuario")
            }

            ownerId?.let { currentOwnerId ->
                val ownerValues = ContentValues().apply {
                    put(DatabaseContract.OwnerTable.COL_FULL_NAME, fullName)
                    put(DatabaseContract.OwnerTable.COL_EMAIL, email)
                    put(DatabaseContract.OwnerTable.COL_PHONE, phone)
                }

                db.update(
                    DatabaseContract.OwnerTable.TABLE_NAME,
                    ownerValues,
                    "${DatabaseContract.OwnerTable.COL_OWNER_ID} = ? AND ${DatabaseContract.OwnerTable.COL_IS_ACTIVE} = 1",
                    arrayOf(currentOwnerId.toString())
                )
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

    fun updatePreferredBranch(userId: Long, branchId: Long): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseContract.UserTable.COL_HOME_BRANCH_ID, branchId)
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
}