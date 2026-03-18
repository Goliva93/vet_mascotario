package pe.goliva.vet_mascotario.data.dao

import android.content.ContentValues
import android.content.Context
import pe.goliva.vet_mascotario.data.db.DatabaseContract
import pe.goliva.vet_mascotario.data.db.DatabaseHelper
import pe.goliva.vet_mascotario.data.model.BreedOption
import pe.goliva.vet_mascotario.data.model.PetDetail
import pe.goliva.vet_mascotario.data.model.PetListItem
import pe.goliva.vet_mascotario.data.model.SpeciesOption
import java.time.LocalDateTime

class PetDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getPetsByUserId(userId: Long): List<PetListItem> {
        val db = dbHelper.readableDatabase
        val pets = mutableListOf<PetListItem>()

        val query = """
            SELECT
                p.${DatabaseContract.PetTable.COL_PET_ID},
                p.${DatabaseContract.PetTable.COL_NAME},
                p.${DatabaseContract.PetTable.COL_SEX},
                p.${DatabaseContract.PetTable.COL_BIRTH_DATE},
                p.${DatabaseContract.PetTable.COL_COLOR},
                p.${DatabaseContract.PetTable.COL_PHOTO_URL},
                s.${DatabaseContract.SpeciesTable.COL_NAME} AS SpeciesName,
                b.${DatabaseContract.BreedTable.COL_NAME} AS BreedName
            FROM ${DatabaseContract.PetTable.TABLE_NAME} p
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON p.${DatabaseContract.PetTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            LEFT JOIN ${DatabaseContract.SpeciesTable.TABLE_NAME} s
                ON p.${DatabaseContract.PetTable.COL_SPECIES_ID} = s.${DatabaseContract.SpeciesTable.COL_SPECIES_ID}
            LEFT JOIN ${DatabaseContract.BreedTable.TABLE_NAME} b
                ON p.${DatabaseContract.PetTable.COL_BREED_ID} = b.${DatabaseContract.BreedTable.COL_BREED_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND p.${DatabaseContract.PetTable.COL_IS_ACTIVE} = 1
            ORDER BY p.${DatabaseContract.PetTable.COL_CREATED_AT} DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        cursor.use {
            while (it.moveToNext()) {
                pets.add(
                    PetListItem(
                        petId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PET_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_NAME)),
                        speciesName = if (it.isNull(it.getColumnIndexOrThrow("SpeciesName"))) null
                        else it.getString(it.getColumnIndexOrThrow("SpeciesName")),
                        breedName = if (it.isNull(it.getColumnIndexOrThrow("BreedName"))) null
                        else it.getString(it.getColumnIndexOrThrow("BreedName")),
                        sex = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SEX))) null
                        else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SEX)),
                        birthDate = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BIRTH_DATE))) null
                        else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BIRTH_DATE)),
                        color = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_COLOR))) null
                        else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_COLOR)),
                        photoUrl = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PHOTO_URL))) null
                        else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PHOTO_URL))
                    )
                )
            }
        }

        return pets
    }

    fun getPetDetailByIdForUser(userId: Long, petId: Long): PetDetail? {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT
                p.${DatabaseContract.PetTable.COL_PET_ID},
                p.${DatabaseContract.PetTable.COL_NAME},
                p.${DatabaseContract.PetTable.COL_SPECIES_ID},
                p.${DatabaseContract.PetTable.COL_BREED_ID},
                p.${DatabaseContract.PetTable.COL_SEX},
                p.${DatabaseContract.PetTable.COL_BIRTH_DATE},
                p.${DatabaseContract.PetTable.COL_COLOR},
                p.${DatabaseContract.PetTable.COL_NOTES},
                p.${DatabaseContract.PetTable.COL_PHOTO_URL},
                s.${DatabaseContract.SpeciesTable.COL_NAME} AS SpeciesName,
                b.${DatabaseContract.BreedTable.COL_NAME} AS BreedName
            FROM ${DatabaseContract.PetTable.TABLE_NAME} p
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON p.${DatabaseContract.PetTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            LEFT JOIN ${DatabaseContract.SpeciesTable.TABLE_NAME} s
                ON p.${DatabaseContract.PetTable.COL_SPECIES_ID} = s.${DatabaseContract.SpeciesTable.COL_SPECIES_ID}
            LEFT JOIN ${DatabaseContract.BreedTable.TABLE_NAME} b
                ON p.${DatabaseContract.PetTable.COL_BREED_ID} = b.${DatabaseContract.BreedTable.COL_BREED_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND p.${DatabaseContract.PetTable.COL_PET_ID} = ?
              AND p.${DatabaseContract.PetTable.COL_IS_ACTIVE} = 1
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), petId.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                return PetDetail(
                    petId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PET_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_NAME)),
                    speciesId = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SPECIES_ID))) null
                    else it.getLong(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SPECIES_ID)),
                    speciesName = if (it.isNull(it.getColumnIndexOrThrow("SpeciesName"))) null
                    else it.getString(it.getColumnIndexOrThrow("SpeciesName")),
                    breedId = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BREED_ID))) null
                    else it.getLong(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BREED_ID)),
                    breedName = if (it.isNull(it.getColumnIndexOrThrow("BreedName"))) null
                    else it.getString(it.getColumnIndexOrThrow("BreedName")),
                    sex = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SEX))) null
                    else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_SEX)),
                    birthDate = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BIRTH_DATE))) null
                    else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_BIRTH_DATE)),
                    color = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_COLOR))) null
                    else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_COLOR)),
                    notes = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_NOTES))) null
                    else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_NOTES)),
                    photoUrl = if (it.isNull(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PHOTO_URL))) null
                    else it.getString(it.getColumnIndexOrThrow(DatabaseContract.PetTable.COL_PHOTO_URL))
                )
            }
        }

        return null
    }

    fun getActiveSpecies(): List<SpeciesOption> {
        val db = dbHelper.readableDatabase
        val species = mutableListOf<SpeciesOption>()

        val query = """
            SELECT
                ${DatabaseContract.SpeciesTable.COL_SPECIES_ID},
                ${DatabaseContract.SpeciesTable.COL_NAME}
            FROM ${DatabaseContract.SpeciesTable.TABLE_NAME}
            ORDER BY ${DatabaseContract.SpeciesTable.COL_NAME} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                species.add(
                    SpeciesOption(
                        speciesId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.SpeciesTable.COL_SPECIES_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseContract.SpeciesTable.COL_NAME))
                    )
                )
            }
        }

        return species
    }

    fun getBreedsBySpecies(speciesId: Long): List<BreedOption> {
        val db = dbHelper.readableDatabase
        val breeds = mutableListOf<BreedOption>()

        val query = """
            SELECT
                ${DatabaseContract.BreedTable.COL_BREED_ID},
                ${DatabaseContract.BreedTable.COL_SPECIES_ID},
                ${DatabaseContract.BreedTable.COL_NAME}
            FROM ${DatabaseContract.BreedTable.TABLE_NAME}
            WHERE ${DatabaseContract.BreedTable.COL_SPECIES_ID} = ?
            ORDER BY ${DatabaseContract.BreedTable.COL_NAME} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(speciesId.toString()))

        cursor.use {
            while (it.moveToNext()) {
                breeds.add(
                    BreedOption(
                        breedId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.BreedTable.COL_BREED_ID)),
                        speciesId = it.getLong(it.getColumnIndexOrThrow(DatabaseContract.BreedTable.COL_SPECIES_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseContract.BreedTable.COL_NAME))
                    )
                )
            }
        }

        return breeds
    }

    fun existsActivePetDuplicate(
        userId: Long,
        petName: String,
        speciesId: Long
    ): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 1
            FROM ${DatabaseContract.PetTable.TABLE_NAME} p
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON p.${DatabaseContract.PetTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND p.${DatabaseContract.PetTable.COL_IS_ACTIVE} = 1
              AND LOWER(TRIM(p.${DatabaseContract.PetTable.COL_NAME})) = LOWER(TRIM(?))
              AND p.${DatabaseContract.PetTable.COL_SPECIES_ID} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            query,
            arrayOf(userId.toString(), petName, speciesId.toString())
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun existsActivePetDuplicateExcludingPet(
        userId: Long,
        petId: Long,
        petName: String,
        speciesId: Long
    ): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 1
            FROM ${DatabaseContract.PetTable.TABLE_NAME} p
            INNER JOIN ${DatabaseContract.UserTable.TABLE_NAME} u
                ON p.${DatabaseContract.PetTable.COL_OWNER_ID} = u.${DatabaseContract.UserTable.COL_OWNER_ID}
            WHERE u.${DatabaseContract.UserTable.COL_USER_ID} = ?
              AND u.${DatabaseContract.UserTable.COL_IS_ACTIVE} = 1
              AND p.${DatabaseContract.PetTable.COL_IS_ACTIVE} = 1
              AND p.${DatabaseContract.PetTable.COL_PET_ID} <> ?
              AND LOWER(TRIM(p.${DatabaseContract.PetTable.COL_NAME})) = LOWER(TRIM(?))
              AND p.${DatabaseContract.PetTable.COL_SPECIES_ID} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            query,
            arrayOf(userId.toString(), petId.toString(), petName, speciesId.toString())
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun insertPetForUser(
        userId: Long,
        petName: String,
        speciesId: Long,
        breedId: Long?,
        sex: String?,
        birthDate: String?,
        color: String?,
        notes: String?,
        photoUrl: String?
    ): Boolean {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        return try {
            val ownerId = getOwnerIdByUserId(db, userId)
                ?: throw IllegalStateException("No se encontró el owner del usuario")

            val values = ContentValues().apply {
                put(DatabaseContract.PetTable.COL_OWNER_ID, ownerId)
                put(DatabaseContract.PetTable.COL_NAME, petName)
                put(DatabaseContract.PetTable.COL_SPECIES_ID, speciesId)

                if (breedId != null) put(DatabaseContract.PetTable.COL_BREED_ID, breedId)
                else putNull(DatabaseContract.PetTable.COL_BREED_ID)

                if (sex.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_SEX)
                else put(DatabaseContract.PetTable.COL_SEX, sex)

                if (birthDate.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_BIRTH_DATE)
                else put(DatabaseContract.PetTable.COL_BIRTH_DATE, birthDate)

                if (color.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_COLOR)
                else put(DatabaseContract.PetTable.COL_COLOR, color)

                if (notes.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_NOTES)
                else put(DatabaseContract.PetTable.COL_NOTES, notes)

                if (photoUrl.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_PHOTO_URL)
                else put(DatabaseContract.PetTable.COL_PHOTO_URL, photoUrl)

                put(DatabaseContract.PetTable.COL_IS_ACTIVE, 1)
                put(DatabaseContract.PetTable.COL_CREATED_AT, nowUtc())
            }

            val insertedId = db.insert(DatabaseContract.PetTable.TABLE_NAME, null, values)
            if (insertedId == -1L) {
                throw IllegalStateException("No se pudo insertar la mascota")
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

    fun updatePetForUser(
        userId: Long,
        petId: Long,
        petName: String,
        speciesId: Long,
        breedId: Long?,
        sex: String?,
        birthDate: String?,
        color: String?,
        notes: String?
    ): Boolean {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        return try {
            val ownerId = getOwnerIdByUserId(db, userId)
                ?: throw IllegalStateException("No se encontró el owner del usuario")

            val values = ContentValues().apply {
                put(DatabaseContract.PetTable.COL_NAME, petName)
                put(DatabaseContract.PetTable.COL_SPECIES_ID, speciesId)

                if (breedId != null) put(DatabaseContract.PetTable.COL_BREED_ID, breedId)
                else putNull(DatabaseContract.PetTable.COL_BREED_ID)

                if (sex.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_SEX)
                else put(DatabaseContract.PetTable.COL_SEX, sex)

                if (birthDate.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_BIRTH_DATE)
                else put(DatabaseContract.PetTable.COL_BIRTH_DATE, birthDate)

                if (color.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_COLOR)
                else put(DatabaseContract.PetTable.COL_COLOR, color)

                if (notes.isNullOrBlank()) putNull(DatabaseContract.PetTable.COL_NOTES)
                else put(DatabaseContract.PetTable.COL_NOTES, notes)
            }

            val updatedRows = db.update(
                DatabaseContract.PetTable.TABLE_NAME,
                values,
                "${DatabaseContract.PetTable.COL_PET_ID} = ? AND ${DatabaseContract.PetTable.COL_OWNER_ID} = ? AND ${DatabaseContract.PetTable.COL_IS_ACTIVE} = 1",
                arrayOf(petId.toString(), ownerId.toString())
            )

            if (updatedRows <= 0) {
                throw IllegalStateException("No se pudo actualizar la mascota")
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

    private fun nowUtc(): String {
        return LocalDateTime.now().toString()
    }
}