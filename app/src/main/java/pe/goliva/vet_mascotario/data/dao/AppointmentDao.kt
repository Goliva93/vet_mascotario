package pe.goliva.vet_mascotario.data.dao

import android.content.ContentValues
import android.content.Context
import pe.goliva.vet_mascotario.data.db.DatabaseContract
import pe.goliva.vet_mascotario.data.db.DatabaseHelper
import pe.goliva.vet_mascotario.data.model.AppointmentDetail
import pe.goliva.vet_mascotario.data.model.AppointmentListItem
import pe.goliva.vet_mascotario.data.model.AppointmentTypeOption
import pe.goliva.vet_mascotario.data.model.HomeUpcomingAppointment
import pe.goliva.vet_mascotario.data.model.TimeSlotOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getAppointmentsByUserId(userId: Long): List<AppointmentListItem> {
        val db = dbHelper.readableDatabase
        val appointments = mutableListOf<AppointmentListItem>()

        val query = """
            SELECT
                a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID},
                p.${DatabaseContract.PetTable.COL_NAME} AS PetName,
                at.${DatabaseContract.AppointmentTypeTable.COL_NAME} AS AppointmentTypeName,
                b.${DatabaseContract.BranchTable.COL_NAME} AS BranchName,
                a.${DatabaseContract.AppointmentTable.COL_START_AT},
                a.${DatabaseContract.AppointmentTable.COL_END_AT},
                a.${DatabaseContract.AppointmentTable.COL_STATUS},
                a.${DatabaseContract.AppointmentTable.COL_NOTES}
            FROM ${DatabaseContract.AppointmentTable.TABLE_NAME} a
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON a.${DatabaseContract.AppointmentTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            INNER JOIN ${DatabaseContract.PetTable.TABLE_NAME} p
                ON a.${DatabaseContract.AppointmentTable.COL_PET_ID} = p.${DatabaseContract.PetTable.COL_PET_ID}
            INNER JOIN ${DatabaseContract.BranchTable.TABLE_NAME} b
                ON a.${DatabaseContract.AppointmentTable.COL_BRANCH_ID} = b.${DatabaseContract.BranchTable.COL_BRANCH_ID}
            INNER JOIN ${DatabaseContract.AppointmentTypeTable.TABLE_NAME} at
                ON a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID} = at.${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
            ORDER BY
                CASE
                    WHEN a.${DatabaseContract.AppointmentTable.COL_STATUS} IN ('PENDING', 'CONFIRMED') THEN 0
                    WHEN a.${DatabaseContract.AppointmentTable.COL_STATUS} = 'FINISHED' THEN 1
                    ELSE 2
                END,
                a.${DatabaseContract.AppointmentTable.COL_START_AT} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        cursor.use {
            while (it.moveToNext()) {
                appointments.add(
                    AppointmentListItem(
                        appointmentId = it.getLong(
                            it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID)
                        ),
                        petName = it.getString(it.getColumnIndexOrThrow("PetName")),
                        appointmentTypeName = it.getString(it.getColumnIndexOrThrow("AppointmentTypeName")),
                        branchName = it.getString(it.getColumnIndexOrThrow("BranchName")),
                        startAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_START_AT)),
                        endAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_END_AT)),
                        status = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_STATUS)),
                        notes = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_NOTES))) {
                            null
                        } else {
                            it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_NOTES))
                        }
                    )
                )
            }
        }

        return appointments
    }

    fun getUpcomingAppointmentByUserId(userId: Long): HomeUpcomingAppointment? {
        val db = dbHelper.readableDatabase
        val now = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        )

        val query = """
            SELECT
                a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID},
                p.${DatabaseContract.PetTable.COL_NAME} AS PetName,
                at.${DatabaseContract.AppointmentTypeTable.COL_NAME} AS AppointmentTypeName,
                b.${DatabaseContract.BranchTable.COL_NAME} AS BranchName,
                a.${DatabaseContract.AppointmentTable.COL_START_AT},
                a.${DatabaseContract.AppointmentTable.COL_END_AT},
                a.${DatabaseContract.AppointmentTable.COL_STATUS}
            FROM ${DatabaseContract.AppointmentTable.TABLE_NAME} a
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON a.${DatabaseContract.AppointmentTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            INNER JOIN ${DatabaseContract.PetTable.TABLE_NAME} p
                ON a.${DatabaseContract.AppointmentTable.COL_PET_ID} = p.${DatabaseContract.PetTable.COL_PET_ID}
            INNER JOIN ${DatabaseContract.BranchTable.TABLE_NAME} b
                ON a.${DatabaseContract.AppointmentTable.COL_BRANCH_ID} = b.${DatabaseContract.BranchTable.COL_BRANCH_ID}
            INNER JOIN ${DatabaseContract.AppointmentTypeTable.TABLE_NAME} at
                ON a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID} = at.${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND a.${DatabaseContract.AppointmentTable.COL_STATUS} IN ('PENDING', 'CONFIRMED')
              AND a.${DatabaseContract.AppointmentTable.COL_START_AT} >= ?
            ORDER BY a.${DatabaseContract.AppointmentTable.COL_START_AT} ASC
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), now))

        cursor.use {
            if (it.moveToFirst()) {
                return HomeUpcomingAppointment(
                    appointmentId = it.getLong(
                        it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID)
                    ),
                    petName = it.getString(it.getColumnIndexOrThrow("PetName")),
                    appointmentTypeName = it.getString(it.getColumnIndexOrThrow("AppointmentTypeName")),
                    branchName = it.getString(it.getColumnIndexOrThrow("BranchName")),
                    startAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_START_AT)),
                    endAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_END_AT)),
                    status = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_STATUS))
                )
            }
        }

        return null
    }

    fun getAppointmentDetailByIdForUser(
        userId: Long,
        appointmentId: Long
    ): AppointmentDetail? {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT
                a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID},
                p.${DatabaseContract.PetTable.COL_NAME} AS PetName,
                at.${DatabaseContract.AppointmentTypeTable.COL_NAME} AS AppointmentTypeName,
                b.${DatabaseContract.BranchTable.COL_NAME} AS BranchName,
                a.${DatabaseContract.AppointmentTable.COL_START_AT},
                a.${DatabaseContract.AppointmentTable.COL_END_AT},
                a.${DatabaseContract.AppointmentTable.COL_STATUS},
                a.${DatabaseContract.AppointmentTable.COL_NOTES},
                a.${DatabaseContract.AppointmentTable.COL_CANCEL_REASON}
            FROM ${DatabaseContract.AppointmentTable.TABLE_NAME} a
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON a.${DatabaseContract.AppointmentTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            INNER JOIN ${DatabaseContract.PetTable.TABLE_NAME} p
                ON a.${DatabaseContract.AppointmentTable.COL_PET_ID} = p.${DatabaseContract.PetTable.COL_PET_ID}
            INNER JOIN ${DatabaseContract.BranchTable.TABLE_NAME} b
                ON a.${DatabaseContract.AppointmentTable.COL_BRANCH_ID} = b.${DatabaseContract.BranchTable.COL_BRANCH_ID}
            INNER JOIN ${DatabaseContract.AppointmentTypeTable.TABLE_NAME} at
                ON a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID} = at.${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND a.${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), appointmentId.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                return AppointmentDetail(
                    appointmentId = it.getLong(
                        it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID)
                    ),
                    petName = it.getString(it.getColumnIndexOrThrow("PetName")),
                    appointmentTypeName = it.getString(it.getColumnIndexOrThrow("AppointmentTypeName")),
                    branchName = it.getString(it.getColumnIndexOrThrow("BranchName")),
                    startAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_START_AT)),
                    endAt = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_END_AT)),
                    status = it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_STATUS)),
                    notes = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_NOTES))) {
                        null
                    } else {
                        it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_NOTES))
                    },
                    cancelReason = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_CANCEL_REASON))) {
                        null
                    } else {
                        it.getString(it.getColumnIndexOrThrow(DatabaseContract.AppointmentTable.COL_CANCEL_REASON))
                    }
                )
            }
        }

        return null
    }

    fun getAppointmentTypes(): List<AppointmentTypeOption> {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<AppointmentTypeOption>()

        val query = """
            SELECT
                ${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID},
                ${DatabaseContract.AppointmentTypeTable.COL_NAME},
                ${DatabaseContract.AppointmentTypeTable.COL_DEFAULT_DURATION_MIN}
            FROM ${DatabaseContract.AppointmentTypeTable.TABLE_NAME}
            ORDER BY ${DatabaseContract.AppointmentTypeTable.COL_NAME} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                items.add(
                    AppointmentTypeOption(
                        appointmentTypeId = it.getLong(
                            it.getColumnIndexOrThrow(DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID)
                        ),
                        name = it.getString(
                            it.getColumnIndexOrThrow(DatabaseContract.AppointmentTypeTable.COL_NAME)
                        ),
                        defaultDurationMin = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseContract.AppointmentTypeTable.COL_DEFAULT_DURATION_MIN)
                        )
                    )
                )
            }
        }

        return items
    }

    fun getAvailableTimeSlots(
        branchId: Long,
        selectedDate: String,
        durationMin: Int
    ): List<TimeSlotOption> {
        val db = dbHelper.readableDatabase

        val date = LocalDate.parse(selectedDate)
        val slotStepMin = 30L
        val openingTime = LocalTime.of(9, 0)
        val closingTime = LocalTime.of(18, 0)

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

        val dayStart = date.atStartOfDay()
        val dayEnd = date.atTime(23, 59, 59)

        val busyRanges = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()

        val query = """
            SELECT
                ${DatabaseContract.AppointmentTable.COL_START_AT},
                ${DatabaseContract.AppointmentTable.COL_END_AT}
            FROM ${DatabaseContract.AppointmentTable.TABLE_NAME}
            WHERE ${DatabaseContract.AppointmentTable.COL_BRANCH_ID} = ?
              AND ${DatabaseContract.AppointmentTable.COL_STATUS} IN ('PENDING', 'CONFIRMED')
              AND ${DatabaseContract.AppointmentTable.COL_START_AT} <= ?
              AND ${DatabaseContract.AppointmentTable.COL_END_AT} >= ?
        """.trimIndent()

        val cursor = db.rawQuery(
            query,
            arrayOf(
                branchId.toString(),
                dayEnd.format(dbFormatter),
                dayStart.format(dbFormatter)
            )
        )

        cursor.use {
            while (it.moveToNext()) {
                val start = LocalDateTime.parse(it.getString(0), inputFormatter)
                val end = LocalDateTime.parse(it.getString(1), inputFormatter)
                busyRanges.add(start to end)
            }
        }

        val slots = mutableListOf<TimeSlotOption>()
        var currentStart = date.atTime(openingTime)

        while (true) {
            val currentEnd = currentStart.plusMinutes(durationMin.toLong())
            if (currentEnd.toLocalTime().isAfter(closingTime)) break

            val overlaps = busyRanges.any { (busyStart, busyEnd) ->
                currentStart < busyEnd && currentEnd > busyStart
            }

            slots.add(
                TimeSlotOption(
                    startTime = currentStart.toLocalTime().format(timeFormatter),
                    endTime = currentEnd.toLocalTime().format(timeFormatter),
                    startAtDb = currentStart.format(dbFormatter),
                    endAtDb = currentEnd.format(dbFormatter),
                    isAvailable = !overlaps
                )
            )

            currentStart = currentStart.plusMinutes(slotStepMin)
        }

        return slots
    }

    fun createAppointmentForUser(
        userId: Long,
        petId: Long,
        appointmentTypeId: Long,
        branchId: Long,
        startAt: String,
        endAt: String,
        notes: String?
    ): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()

        return try {
            val ownerId = getOwnerIdByUserId(db, userId)
                ?: throw IllegalStateException("No se encontró owner para el usuario")

            val channelId = getChannelIdByCode(db, DatabaseContract.Defaults.CHANNEL_APP)
                ?: throw IllegalStateException("No se encontró el canal APP")

            val values = ContentValues().apply {
                put(DatabaseContract.AppointmentTable.COL_BRANCH_ID, branchId)
                put(DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID, appointmentTypeId)
                put(DatabaseContract.AppointmentTable.COL_CHANNEL_ID, channelId)
                put(DatabaseContract.AppointmentTable.COL_OWNER_ID, ownerId)
                put(DatabaseContract.AppointmentTable.COL_PET_ID, petId)
                put(DatabaseContract.AppointmentTable.COL_START_AT, startAt)
                put(DatabaseContract.AppointmentTable.COL_END_AT, endAt)
                put(
                    DatabaseContract.AppointmentTable.COL_STATUS,
                    DatabaseContract.Defaults.APPOINTMENT_STATUS_PENDING
                )
                if (notes.isNullOrBlank()) {
                    putNull(DatabaseContract.AppointmentTable.COL_NOTES)
                } else {
                    put(DatabaseContract.AppointmentTable.COL_NOTES, notes)
                }
                put(DatabaseContract.AppointmentTable.COL_CREATED_BY_USER_ID, userId)
                put(DatabaseContract.AppointmentTable.COL_CREATED_AT, nowUtc())
                put(DatabaseContract.AppointmentTable.COL_UPDATED_AT, nowUtc())
            }

            val insertedId = db.insert(
                DatabaseContract.AppointmentTable.TABLE_NAME,
                null,
                values
            )

            if (insertedId == -1L) {
                throw IllegalStateException("No se pudo registrar la cita")
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

    private fun getOwnerIdByUserId(
        db: android.database.sqlite.SQLiteDatabase,
        userId: Long
    ): Long? {
        val query = """
            SELECT ${DatabaseContract.UserTable.COL_OWNER_ID}
            FROM ${DatabaseContract.UserTable.TABLE_NAME}
            WHERE ${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND ${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        cursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                return it.getLong(0)
            }
        }
        return null
    }

    private fun getChannelIdByCode(
        db: android.database.sqlite.SQLiteDatabase,
        code: String
    ): Long? {
        val query = """
            SELECT ${DatabaseContract.ChannelTable.COL_CHANNEL_ID}
            FROM ${DatabaseContract.ChannelTable.TABLE_NAME}
            WHERE ${DatabaseContract.ChannelTable.COL_CODE} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(code))
        cursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                return it.getLong(0)
            }
        }
        return null
    }

    private fun nowUtc(): String {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        )
    }
}