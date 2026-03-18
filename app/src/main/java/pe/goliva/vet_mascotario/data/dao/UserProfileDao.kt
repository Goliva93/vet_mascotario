package pe.goliva.vet_mascotario.data.dao

import android.content.Context
import pe.goliva.vet_mascotario.data.db.DatabaseContract
import pe.goliva.vet_mascotario.data.db.DatabaseHelper
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
}