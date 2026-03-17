package pe.goliva.vet_mascotario.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper
 *
 * Esta clase sí crea físicamente la base de datos SQLite.
 *
 * Hereda de SQLiteOpenHelper, que nos pide implementar:
 * - onCreate()  -> se ejecuta cuando la BD se crea por primera vez
 * - onUpgrade() -> se ejecuta cuando cambia la versión de la BD
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {

    /**
     * onConfigure()
     *
     * Aquí activamos las foreign keys en SQLite.
     * Por defecto SQLite no siempre las aplica si no las habilitas explícitamente.
     */
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    /**
     * onCreate()
     *
     * Este método se ejecuta cuando la base de datos aún no existe.
     * Aquí:
     * 1. creamos tablas
     * 2. insertamos datos semilla
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Orden importante:
        // primero tablas "padre", luego tablas que dependen de esas llaves foráneas.
        db.execSQL(createBranchTable())
        db.execSQL(createBranchSettingTable())

        db.execSQL(createOwnerTable())
        db.execSQL(createUserTable())

        db.execSQL(createSpeciesTable())
        db.execSQL(createBreedTable())

        db.execSQL(createChannelTable())
        db.execSQL(createAppointmentTypeTable())
        db.execSQL(createServiceCatalogTable())

        db.execSQL(createPetTable())
        db.execSQL(createAppointmentTable())
        db.execSQL(createAppointmentServiceTable())
        db.execSQL(createClinicalVisitTable())

        db.execSQL(createPaymentTable())
        db.execSQL(createPaymentItemTable())
        db.execSQL(createPaymentEvidenceTable())

        db.execSQL(createNotificationChannelTable())
        db.execSQL(createNotificationTypeTable())
        db.execSQL(createNotificationPreferenceTable())
        db.execSQL(createNotificationTable())

        seedInitialData(db)
    }

    /**
     * onUpgrade()
     *
     * Como estamos en una etapa inicial del proyecto,
     * si cambia la versión, borramos y recreamos todo.
     *
     * Más adelante, en un proyecto más maduro,
     * esto se reemplaza por migraciones formales.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.NotificationTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.NotificationPreferenceTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.NotificationTypeTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.NotificationChannelTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.PaymentEvidenceTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.PaymentItemTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.PaymentTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ClinicalVisitTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.AppointmentServiceTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.AppointmentTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.PetTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ServiceCatalogTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.AppointmentTypeTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ChannelTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.BreedTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.SpeciesTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.UserTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.OwnerTable.TABLE_NAME}")

        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.BranchSettingTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.BranchTable.TABLE_NAME}")

        onCreate(db)
    }

    // =========================================================
    // CREATE TABLES
    // =========================================================

    private fun createBranchTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.BranchTable.TABLE_NAME} (
                ${DatabaseContract.BranchTable.COL_BRANCH_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.BranchTable.COL_NAME} TEXT NOT NULL,
                ${DatabaseContract.BranchTable.COL_ADDRESS} TEXT,
                ${DatabaseContract.BranchTable.COL_PHONE} TEXT,
                ${DatabaseContract.BranchTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.BranchTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
    }

    private fun createBranchSettingTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.BranchSettingTable.TABLE_NAME} (
                ${DatabaseContract.BranchSettingTable.COL_BRANCH_ID} INTEGER PRIMARY KEY,
                ${DatabaseContract.BranchSettingTable.COL_CANCEL_CUTOFF_RULE} TEXT NOT NULL DEFAULT 'DAY_BEFORE_MIDNIGHT',
                ${DatabaseContract.BranchSettingTable.COL_DEPOSIT_AMOUNT} REAL NOT NULL DEFAULT 5.00,
                ${DatabaseContract.BranchSettingTable.COL_DEPOSIT_REQUIRED} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.BranchSettingTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(${DatabaseContract.BranchSettingTable.COL_BRANCH_ID})
                    REFERENCES ${DatabaseContract.BranchTable.TABLE_NAME}(${DatabaseContract.BranchTable.COL_BRANCH_ID})
            )
        """.trimIndent()
    }

    private fun createOwnerTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.OwnerTable.TABLE_NAME} (
                ${DatabaseContract.OwnerTable.COL_OWNER_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.OwnerTable.COL_DOC_TYPE} TEXT,
                ${DatabaseContract.OwnerTable.COL_DOC_NUMBER} TEXT,
                ${DatabaseContract.OwnerTable.COL_FULL_NAME} TEXT NOT NULL,
                ${DatabaseContract.OwnerTable.COL_PHONE} TEXT,
                ${DatabaseContract.OwnerTable.COL_EMAIL} TEXT,
                ${DatabaseContract.OwnerTable.COL_ADDRESS} TEXT,
                ${DatabaseContract.OwnerTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.OwnerTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
    }

    private fun createUserTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.UserTable.TABLE_NAME} (
                ${DatabaseContract.UserTable.COL_USER_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.UserTable.COL_HOME_BRANCH_ID} INTEGER,
                ${DatabaseContract.UserTable.COL_OWNER_ID} INTEGER,
                ${DatabaseContract.UserTable.COL_FULL_NAME} TEXT NOT NULL,
                ${DatabaseContract.UserTable.COL_EMAIL} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.UserTable.COL_PHONE} TEXT,
                ${DatabaseContract.UserTable.COL_PASSWORD_HASH} TEXT,
                ${DatabaseContract.UserTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.UserTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.UserTable.COL_LAST_LOGIN_AT} TEXT,
                FOREIGN KEY(${DatabaseContract.UserTable.COL_HOME_BRANCH_ID})
                    REFERENCES ${DatabaseContract.BranchTable.TABLE_NAME}(${DatabaseContract.BranchTable.COL_BRANCH_ID}),
                FOREIGN KEY(${DatabaseContract.UserTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID})
            )
        """.trimIndent()
    }

    private fun createSpeciesTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.SpeciesTable.TABLE_NAME} (
                ${DatabaseContract.SpeciesTable.COL_SPECIES_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.SpeciesTable.COL_NAME} TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
    }

    private fun createBreedTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.BreedTable.TABLE_NAME} (
                ${DatabaseContract.BreedTable.COL_BREED_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.BreedTable.COL_SPECIES_ID} INTEGER NOT NULL,
                ${DatabaseContract.BreedTable.COL_NAME} TEXT NOT NULL,
                FOREIGN KEY(${DatabaseContract.BreedTable.COL_SPECIES_ID})
                    REFERENCES ${DatabaseContract.SpeciesTable.TABLE_NAME}(${DatabaseContract.SpeciesTable.COL_SPECIES_ID})
            )
        """.trimIndent()
    }

    private fun createChannelTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.ChannelTable.TABLE_NAME} (
                ${DatabaseContract.ChannelTable.COL_CHANNEL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.ChannelTable.COL_CODE} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.ChannelTable.COL_NAME} TEXT NOT NULL
            )
        """.trimIndent()
    }

    private fun createAppointmentTypeTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.AppointmentTypeTable.TABLE_NAME} (
                ${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.AppointmentTypeTable.COL_CODE} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.AppointmentTypeTable.COL_NAME} TEXT NOT NULL,
                ${DatabaseContract.AppointmentTypeTable.COL_DEFAULT_DURATION_MIN} INTEGER NOT NULL
            )
        """.trimIndent()
    }

    private fun createServiceCatalogTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.ServiceCatalogTable.TABLE_NAME} (
                ${DatabaseContract.ServiceCatalogTable.COL_SERVICE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.ServiceCatalogTable.COL_CODE} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.ServiceCatalogTable.COL_NAME} TEXT NOT NULL,
                ${DatabaseContract.ServiceCatalogTable.COL_CATEGORY} TEXT NOT NULL,
                ${DatabaseContract.ServiceCatalogTable.COL_BASE_PRICE} REAL NOT NULL DEFAULT 0,
                ${DatabaseContract.ServiceCatalogTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
    }

    private fun createPetTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.PetTable.TABLE_NAME} (
                ${DatabaseContract.PetTable.COL_PET_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.PetTable.COL_OWNER_ID} INTEGER NOT NULL,
                ${DatabaseContract.PetTable.COL_NAME} TEXT NOT NULL,
                ${DatabaseContract.PetTable.COL_SPECIES_ID} INTEGER NOT NULL,
                ${DatabaseContract.PetTable.COL_BREED_ID} INTEGER,
                ${DatabaseContract.PetTable.COL_SEX} TEXT,
                ${DatabaseContract.PetTable.COL_BIRTH_DATE} TEXT,
                ${DatabaseContract.PetTable.COL_COLOR} TEXT,
                ${DatabaseContract.PetTable.COL_NOTES} TEXT,
                ${DatabaseContract.PetTable.COL_PHOTO_URL} TEXT,
                ${DatabaseContract.PetTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.PetTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(${DatabaseContract.PetTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID}),
                FOREIGN KEY(${DatabaseContract.PetTable.COL_SPECIES_ID})
                    REFERENCES ${DatabaseContract.SpeciesTable.TABLE_NAME}(${DatabaseContract.SpeciesTable.COL_SPECIES_ID}),
                FOREIGN KEY(${DatabaseContract.PetTable.COL_BREED_ID})
                    REFERENCES ${DatabaseContract.BreedTable.TABLE_NAME}(${DatabaseContract.BreedTable.COL_BREED_ID})
            )
        """.trimIndent()
    }

    private fun createAppointmentTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.AppointmentTable.TABLE_NAME} (
                ${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.AppointmentTable.COL_BRANCH_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_CHANNEL_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_OWNER_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_PET_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_ASSIGNED_VET_USER_ID} INTEGER,
                ${DatabaseContract.AppointmentTable.COL_ASSIGNED_GROOMER_USER_ID} INTEGER,
                ${DatabaseContract.AppointmentTable.COL_START_AT} TEXT NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_END_AT} TEXT NOT NULL,
                ${DatabaseContract.AppointmentTable.COL_STATUS} TEXT NOT NULL DEFAULT 'CONFIRMED',
                ${DatabaseContract.AppointmentTable.COL_NOTES} TEXT,
                ${DatabaseContract.AppointmentTable.COL_RESCHEDULED_FROM_APPOINTMENT_ID} INTEGER,
                ${DatabaseContract.AppointmentTable.COL_CANCEL_REASON} TEXT,
                ${DatabaseContract.AppointmentTable.COL_CANCELLED_AT} TEXT,
                ${DatabaseContract.AppointmentTable.COL_CANCELLED_BY_USER_ID} INTEGER,
                ${DatabaseContract.AppointmentTable.COL_CREATED_BY_USER_ID} INTEGER,
                ${DatabaseContract.AppointmentTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.AppointmentTable.COL_UPDATED_AT} TEXT,
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_BRANCH_ID})
                    REFERENCES ${DatabaseContract.BranchTable.TABLE_NAME}(${DatabaseContract.BranchTable.COL_BRANCH_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID})
                    REFERENCES ${DatabaseContract.AppointmentTypeTable.TABLE_NAME}(${DatabaseContract.AppointmentTypeTable.COL_APPOINTMENT_TYPE_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_CHANNEL_ID})
                    REFERENCES ${DatabaseContract.ChannelTable.TABLE_NAME}(${DatabaseContract.ChannelTable.COL_CHANNEL_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_PET_ID})
                    REFERENCES ${DatabaseContract.PetTable.TABLE_NAME}(${DatabaseContract.PetTable.COL_PET_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_ASSIGNED_VET_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_ASSIGNED_GROOMER_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_RESCHEDULED_FROM_APPOINTMENT_ID})
                    REFERENCES ${DatabaseContract.AppointmentTable.TABLE_NAME}(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_CANCELLED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentTable.COL_CREATED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID})
            )
        """.trimIndent()
    }

    private fun createAppointmentServiceTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.AppointmentServiceTable.TABLE_NAME} (
                ${DatabaseContract.AppointmentServiceTable.COL_APPOINTMENT_SERVICE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.AppointmentServiceTable.COL_APPOINTMENT_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentServiceTable.COL_SERVICE_ID} INTEGER NOT NULL,
                ${DatabaseContract.AppointmentServiceTable.COL_QUANTITY} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.AppointmentServiceTable.COL_UNIT_PRICE} REAL NOT NULL DEFAULT 0,
                ${DatabaseContract.AppointmentServiceTable.COL_NOTES} TEXT,
                FOREIGN KEY(${DatabaseContract.AppointmentServiceTable.COL_APPOINTMENT_ID})
                    REFERENCES ${DatabaseContract.AppointmentTable.TABLE_NAME}(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID}),
                FOREIGN KEY(${DatabaseContract.AppointmentServiceTable.COL_SERVICE_ID})
                    REFERENCES ${DatabaseContract.ServiceCatalogTable.TABLE_NAME}(${DatabaseContract.ServiceCatalogTable.COL_SERVICE_ID})
            )
        """.trimIndent()
    }

    private fun createClinicalVisitTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.ClinicalVisitTable.TABLE_NAME} (
                ${DatabaseContract.ClinicalVisitTable.COL_VISIT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.ClinicalVisitTable.COL_BRANCH_ID} INTEGER NOT NULL,
                ${DatabaseContract.ClinicalVisitTable.COL_APPOINTMENT_ID} INTEGER,
                ${DatabaseContract.ClinicalVisitTable.COL_OWNER_ID} INTEGER NOT NULL,
                ${DatabaseContract.ClinicalVisitTable.COL_PET_ID} INTEGER NOT NULL,
                ${DatabaseContract.ClinicalVisitTable.COL_VET_USER_ID} INTEGER,
                ${DatabaseContract.ClinicalVisitTable.COL_VISIT_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.ClinicalVisitTable.COL_NOTES_PRIVATE} TEXT,
                ${DatabaseContract.ClinicalVisitTable.COL_SUMMARY_FOR_CLIENT} TEXT,
                ${DatabaseContract.ClinicalVisitTable.COL_NOTES_FOR_CLIENT} TEXT,
                ${DatabaseContract.ClinicalVisitTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(${DatabaseContract.ClinicalVisitTable.COL_BRANCH_ID})
                    REFERENCES ${DatabaseContract.BranchTable.TABLE_NAME}(${DatabaseContract.BranchTable.COL_BRANCH_ID}),
                FOREIGN KEY(${DatabaseContract.ClinicalVisitTable.COL_APPOINTMENT_ID})
                    REFERENCES ${DatabaseContract.AppointmentTable.TABLE_NAME}(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID}),
                FOREIGN KEY(${DatabaseContract.ClinicalVisitTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID}),
                FOREIGN KEY(${DatabaseContract.ClinicalVisitTable.COL_PET_ID})
                    REFERENCES ${DatabaseContract.PetTable.TABLE_NAME}(${DatabaseContract.PetTable.COL_PET_ID}),
                FOREIGN KEY(${DatabaseContract.ClinicalVisitTable.COL_VET_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID})
            )
        """.trimIndent()
    }

    private fun createPaymentTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.PaymentTable.TABLE_NAME} (
                ${DatabaseContract.PaymentTable.COL_PAYMENT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.PaymentTable.COL_BRANCH_ID} INTEGER NOT NULL,
                ${DatabaseContract.PaymentTable.COL_APPOINTMENT_ID} INTEGER NOT NULL,
                ${DatabaseContract.PaymentTable.COL_OWNER_ID} INTEGER NOT NULL,
                ${DatabaseContract.PaymentTable.COL_METHOD} TEXT NOT NULL,
                ${DatabaseContract.PaymentTable.COL_STATUS} TEXT NOT NULL DEFAULT 'REGISTERED',
                ${DatabaseContract.PaymentTable.COL_AMOUNT} REAL NOT NULL,
                ${DatabaseContract.PaymentTable.COL_PAID_AT} TEXT,
                ${DatabaseContract.PaymentTable.COL_REGISTERED_BY_USER_ID} INTEGER,
                ${DatabaseContract.PaymentTable.COL_REGISTERED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.PaymentTable.COL_VALIDATED_BY_USER_ID} INTEGER,
                ${DatabaseContract.PaymentTable.COL_VALIDATED_AT} TEXT,
                ${DatabaseContract.PaymentTable.COL_VOIDED_BY_USER_ID} INTEGER,
                ${DatabaseContract.PaymentTable.COL_VOIDED_AT} TEXT,
                ${DatabaseContract.PaymentTable.COL_VOID_REASON} TEXT,
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_BRANCH_ID})
                    REFERENCES ${DatabaseContract.BranchTable.TABLE_NAME}(${DatabaseContract.BranchTable.COL_BRANCH_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_APPOINTMENT_ID})
                    REFERENCES ${DatabaseContract.AppointmentTable.TABLE_NAME}(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_REGISTERED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_VALIDATED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentTable.COL_VOIDED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID})
            )
        """.trimIndent()
    }

    private fun createPaymentItemTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.PaymentItemTable.TABLE_NAME} (
                ${DatabaseContract.PaymentItemTable.COL_PAYMENT_ITEM_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.PaymentItemTable.COL_PAYMENT_ID} INTEGER NOT NULL,
                ${DatabaseContract.PaymentItemTable.COL_APPOINTMENT_SERVICE_ID} INTEGER,
                ${DatabaseContract.PaymentItemTable.COL_CONCEPT_NAME} TEXT NOT NULL,
                ${DatabaseContract.PaymentItemTable.COL_AMOUNT} REAL NOT NULL,
                FOREIGN KEY(${DatabaseContract.PaymentItemTable.COL_PAYMENT_ID})
                    REFERENCES ${DatabaseContract.PaymentTable.TABLE_NAME}(${DatabaseContract.PaymentTable.COL_PAYMENT_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentItemTable.COL_APPOINTMENT_SERVICE_ID})
                    REFERENCES ${DatabaseContract.AppointmentServiceTable.TABLE_NAME}(${DatabaseContract.AppointmentServiceTable.COL_APPOINTMENT_SERVICE_ID})
            )
        """.trimIndent()
    }

    private fun createPaymentEvidenceTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.PaymentEvidenceTable.TABLE_NAME} (
                ${DatabaseContract.PaymentEvidenceTable.COL_EVIDENCE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.PaymentEvidenceTable.COL_PAYMENT_ID} INTEGER NOT NULL,
                ${DatabaseContract.PaymentEvidenceTable.COL_FILE_URL} TEXT NOT NULL,
                ${DatabaseContract.PaymentEvidenceTable.COL_FILE_NAME} TEXT,
                ${DatabaseContract.PaymentEvidenceTable.COL_CONTENT_TYPE} TEXT,
                ${DatabaseContract.PaymentEvidenceTable.COL_UPLOADED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.PaymentEvidenceTable.COL_UPLOADED_BY_USER_ID} INTEGER,
                FOREIGN KEY(${DatabaseContract.PaymentEvidenceTable.COL_PAYMENT_ID})
                    REFERENCES ${DatabaseContract.PaymentTable.TABLE_NAME}(${DatabaseContract.PaymentTable.COL_PAYMENT_ID}),
                FOREIGN KEY(${DatabaseContract.PaymentEvidenceTable.COL_UPLOADED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID})
            )
        """.trimIndent()
    }

    private fun createNotificationChannelTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.NotificationChannelTable.TABLE_NAME} (
                ${DatabaseContract.NotificationChannelTable.COL_NOTIFICATION_CHANNEL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.NotificationChannelTable.COL_CODE} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.NotificationChannelTable.COL_NAME} TEXT NOT NULL
            )
        """.trimIndent()
    }

    private fun createNotificationTypeTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.NotificationTypeTable.TABLE_NAME} (
                ${DatabaseContract.NotificationTypeTable.COL_NOTIFICATION_TYPE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.NotificationTypeTable.COL_CODE} TEXT NOT NULL UNIQUE,
                ${DatabaseContract.NotificationTypeTable.COL_NAME} TEXT NOT NULL,
                ${DatabaseContract.NotificationTypeTable.COL_IS_MARKETING} INTEGER NOT NULL DEFAULT 0,
                ${DatabaseContract.NotificationTypeTable.COL_IS_ACTIVE} INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
    }

    private fun createNotificationPreferenceTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.NotificationPreferenceTable.TABLE_NAME} (
                ${DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_PREFERENCE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.NotificationPreferenceTable.COL_USER_ID} INTEGER NOT NULL,
                ${DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_TYPE_ID} INTEGER NOT NULL,
                ${DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_CHANNEL_ID} INTEGER NOT NULL,
                ${DatabaseContract.NotificationPreferenceTable.COL_IS_ENABLED} INTEGER NOT NULL DEFAULT 1,
                ${DatabaseContract.NotificationPreferenceTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(${DatabaseContract.NotificationPreferenceTable.COL_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_TYPE_ID})
                    REFERENCES ${DatabaseContract.NotificationTypeTable.TABLE_NAME}(${DatabaseContract.NotificationTypeTable.COL_NOTIFICATION_TYPE_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_CHANNEL_ID})
                    REFERENCES ${DatabaseContract.NotificationChannelTable.TABLE_NAME}(${DatabaseContract.NotificationChannelTable.COL_NOTIFICATION_CHANNEL_ID})
            )
        """.trimIndent()
    }

    private fun createNotificationTable(): String {
        return """
            CREATE TABLE ${DatabaseContract.NotificationTable.TABLE_NAME} (
                ${DatabaseContract.NotificationTable.COL_NOTIFICATION_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.NotificationTable.COL_USER_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_OWNER_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_APPOINTMENT_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_PAYMENT_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_VISIT_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_NOTIFICATION_TYPE_ID} INTEGER NOT NULL,
                ${DatabaseContract.NotificationTable.COL_NOTIFICATION_CHANNEL_ID} INTEGER NOT NULL,
                ${DatabaseContract.NotificationTable.COL_TITLE} TEXT NOT NULL,
                ${DatabaseContract.NotificationTable.COL_MESSAGE} TEXT NOT NULL,
                ${DatabaseContract.NotificationTable.COL_DESTINATION_ADDRESS} TEXT,
                ${DatabaseContract.NotificationTable.COL_STATUS} TEXT NOT NULL DEFAULT 'PENDING',
                ${DatabaseContract.NotificationTable.COL_SCHEDULED_AT} TEXT,
                ${DatabaseContract.NotificationTable.COL_SENT_AT} TEXT,
                ${DatabaseContract.NotificationTable.COL_DELIVERED_AT} TEXT,
                ${DatabaseContract.NotificationTable.COL_READ_AT} TEXT,
                ${DatabaseContract.NotificationTable.COL_FAILED_AT} TEXT,
                ${DatabaseContract.NotificationTable.COL_ERROR_MESSAGE} TEXT,
                ${DatabaseContract.NotificationTable.COL_EXTERNAL_MESSAGE_ID} TEXT,
                ${DatabaseContract.NotificationTable.COL_CREATED_BY_USER_ID} INTEGER,
                ${DatabaseContract.NotificationTable.COL_CREATED_AT} TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_OWNER_ID})
                    REFERENCES ${DatabaseContract.OwnerTable.TABLE_NAME}(${DatabaseContract.OwnerTable.COL_OWNER_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_APPOINTMENT_ID})
                    REFERENCES ${DatabaseContract.AppointmentTable.TABLE_NAME}(${DatabaseContract.AppointmentTable.COL_APPOINTMENT_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_PAYMENT_ID})
                    REFERENCES ${DatabaseContract.PaymentTable.TABLE_NAME}(${DatabaseContract.PaymentTable.COL_PAYMENT_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_VISIT_ID})
                    REFERENCES ${DatabaseContract.ClinicalVisitTable.TABLE_NAME}(${DatabaseContract.ClinicalVisitTable.COL_VISIT_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_NOTIFICATION_TYPE_ID})
                    REFERENCES ${DatabaseContract.NotificationTypeTable.TABLE_NAME}(${DatabaseContract.NotificationTypeTable.COL_NOTIFICATION_TYPE_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_NOTIFICATION_CHANNEL_ID})
                    REFERENCES ${DatabaseContract.NotificationChannelTable.TABLE_NAME}(${DatabaseContract.NotificationChannelTable.COL_NOTIFICATION_CHANNEL_ID}),
                FOREIGN KEY(${DatabaseContract.NotificationTable.COL_CREATED_BY_USER_ID})
                    REFERENCES ${DatabaseContract.UserTable.TABLE_NAME}(${DatabaseContract.UserTable.COL_USER_ID})
            )
        """.trimIndent()
    }

    // =========================================================
    // SEED DATA
    // =========================================================

    /**
     * Inserta los datos iniciales de demostración.
     * Así la app no arranca vacía.
     */
    private fun seedInitialData(db: SQLiteDatabase) {
        insertBranches(db)
        insertBranchSettings(db)

        val ownerId = insertDemoOwner(db)
        val userId = insertDemoUser(db, ownerId)

        insertSpecies(db)
        insertBreeds(db)

        val channelId = insertAppChannel(db)
        val pushChannelId = insertPushNotificationChannel(db)

        val vetTypeId = insertAppointmentType(db, DatabaseContract.Defaults.APPOINTMENT_TYPE_VET, "Consulta médica", 60)
        val groomingTypeId = insertAppointmentType(db, DatabaseContract.Defaults.APPOINTMENT_TYPE_GROOMING, "Grooming", 120)
        val controlTypeId = insertAppointmentType(db, DatabaseContract.Defaults.APPOINTMENT_TYPE_CONTROL, "Control", 30)

        val serviceVetId = insertService(db, "CONSULTA", "Consulta médica", "VET", 60.0)
        val serviceGroomingId = insertService(db, "GROOMING", "Grooming", "GROOMING", 45.0)
        val serviceControlId = insertService(db, "CONTROL", "Control post atención", "VET", 30.0)

        val pet1Id = insertPet(
            db = db,
            ownerId = ownerId,
            name = "Luna",
            speciesId = 1L,
            breedId = 1L,
            sex = "F",
            birthDate = "2022-03-15",
            color = "Blanco con café",
            notes = "Muy activa",
            photoUrl = ""
        )

        val pet2Id = insertPet(
            db = db,
            ownerId = ownerId,
            name = "Michi",
            speciesId = 2L,
            breedId = 3L,
            sex = "M",
            birthDate = "2023-01-10",
            color = "Gris",
            notes = "Tranquilo",
            photoUrl = ""
        )

        val pendingAppointmentId = insertAppointment(
            db = db,
            branchId = 1L,
            appointmentTypeId = vetTypeId,
            channelId = channelId,
            ownerId = ownerId,
            petId = pet1Id,
            startAt = "2026-03-20 10:00:00",
            endAt = "2026-03-20 11:00:00",
            status = DatabaseContract.Defaults.APPOINTMENT_STATUS_PENDING,
            notes = "Revisión general"
        )

        val finishedAppointmentId = insertAppointment(
            db = db,
            branchId = 2L,
            appointmentTypeId = controlTypeId,
            channelId = channelId,
            ownerId = ownerId,
            petId = pet2Id,
            startAt = "2026-03-10 16:00:00",
            endAt = "2026-03-10 16:30:00",
            status = DatabaseContract.Defaults.APPOINTMENT_STATUS_FINISHED,
            notes = "Control de seguimiento"
        )

        val pendingAppointmentServiceId = insertAppointmentService(db, pendingAppointmentId, serviceVetId, 1, 60.0, "Consulta principal")
        val finishedAppointmentServiceId = insertAppointmentService(db, finishedAppointmentId, serviceControlId, 1, 30.0, "Control de seguimiento")

        val visitId = insertClinicalVisit(
            db = db,
            branchId = 2L,
            appointmentId = finishedAppointmentId,
            ownerId = ownerId,
            petId = pet2Id,
            vetUserId = null,
            summaryForClient = "Se realizó control general. La mascota se encuentra estable.",
            notesForClient = "Continuar con la medicación y volver si presenta molestias.",
            notesPrivate = "Observación interna de ejemplo."
        )

        val paymentId = insertPayment(
            db = db,
            branchId = 2L,
            appointmentId = finishedAppointmentId,
            ownerId = ownerId,
            method = "Yape",
            status = DatabaseContract.Defaults.PAYMENT_STATUS_VALIDATED,
            amount = 45.0,
            paidAt = "2026-03-10 17:00:00",
            registeredByUserId = userId,
            validatedByUserId = userId
        )

        insertPaymentItem(db, paymentId, finishedAppointmentServiceId, "Control de seguimiento", 30.0)
        insertPaymentItem(db, paymentId, null, "Medicamento", 10.0)
        insertPaymentItem(db, paymentId, null, "Limpieza de oído", 5.0)

        insertPaymentEvidence(
            db,
            paymentId,
            fileUrl = "content://demo/payment/evidence1.jpg",
            fileName = "yape_comprobante.jpg",
            contentType = "image/jpeg",
            uploadedByUserId = userId
        )

        val notifTypeCreatedId = insertNotificationType(db, "APPOINTMENT_CREATED", "Cita creada", 0, 1)
        val notifTypeReminderId = insertNotificationType(db, "APPOINTMENT_REMINDER", "Recordatorio de cita", 0, 1)
        val notifTypeSummaryId = insertNotificationType(db, "VISIT_SUMMARY", "Resumen de atención", 0, 1)
        val notifTypePaymentId = insertNotificationType(db, "PAYMENT_VALIDATED", "Pago validado", 0, 1)

        insertNotificationPreference(db, userId, notifTypeCreatedId, pushChannelId, 1)
        insertNotificationPreference(db, userId, notifTypeReminderId, pushChannelId, 1)
        insertNotificationPreference(db, userId, notifTypeSummaryId, pushChannelId, 1)
        insertNotificationPreference(db, userId, notifTypePaymentId, pushChannelId, 1)

        insertNotification(
            db = db,
            userId = userId,
            ownerId = ownerId,
            appointmentId = pendingAppointmentId,
            paymentId = null,
            visitId = null,
            notificationTypeId = notifTypeCreatedId,
            notificationChannelId = pushChannelId,
            title = "Cita creada",
            message = "Tu cita para Luna fue registrada correctamente.",
            destinationAddress = "demo@mascotario.com",
            status = DatabaseContract.Defaults.NOTIFICATION_STATUS_SENT
        )

        insertNotification(
            db = db,
            userId = userId,
            ownerId = ownerId,
            appointmentId = pendingAppointmentId,
            paymentId = null,
            visitId = null,
            notificationTypeId = notifTypeReminderId,
            notificationChannelId = pushChannelId,
            title = "Recordatorio de cita",
            message = "Recuerda tu cita pendiente en sede Chorrillos.",
            destinationAddress = "demo@mascotario.com",
            status = DatabaseContract.Defaults.NOTIFICATION_STATUS_DELIVERED
        )

        insertNotification(
            db = db,
            userId = userId,
            ownerId = ownerId,
            appointmentId = finishedAppointmentId,
            paymentId = null,
            visitId = visitId,
            notificationTypeId = notifTypeSummaryId,
            notificationChannelId = pushChannelId,
            title = "Resumen de atención",
            message = "Ya puedes revisar el resumen de atención de tu última cita.",
            destinationAddress = "demo@mascotario.com",
            status = DatabaseContract.Defaults.NOTIFICATION_STATUS_READ
        )

        insertNotification(
            db = db,
            userId = userId,
            ownerId = ownerId,
            appointmentId = finishedAppointmentId,
            paymentId = paymentId,
            visitId = null,
            notificationTypeId = notifTypePaymentId,
            notificationChannelId = pushChannelId,
            title = "Pago validado",
            message = "El pago de tu última cita fue validado correctamente.",
            destinationAddress = "demo@mascotario.com",
            status = DatabaseContract.Defaults.NOTIFICATION_STATUS_DELIVERED
        )
    }

    // =========================================================
    // HELPERS DE INSERT
    // =========================================================

    private fun insertBranches(db: SQLiteDatabase) {
        insertBranch(db, "Chorrillos", "Av. Defensores 123", "999111111")
        insertBranch(db, "Surco", "Av. Caminos del Inca 456", "999222222")
        insertBranch(db, "Barranco", "Jr. Unión 789", "999333333")
    }

    private fun insertBranch(db: SQLiteDatabase, name: String, address: String, phone: String): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.BranchTable.COL_NAME, name)
            put(DatabaseContract.BranchTable.COL_ADDRESS, address)
            put(DatabaseContract.BranchTable.COL_PHONE, phone)
            put(DatabaseContract.BranchTable.COL_IS_ACTIVE, 1)
            put(DatabaseContract.BranchTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.BranchTable.TABLE_NAME, null, values)
    }

    private fun insertBranchSettings(db: SQLiteDatabase) {
        for (branchId in 1..3) {
            val values = ContentValues().apply {
                put(DatabaseContract.BranchSettingTable.COL_BRANCH_ID, branchId)
                put(DatabaseContract.BranchSettingTable.COL_CANCEL_CUTOFF_RULE, "DAY_BEFORE_MIDNIGHT")
                put(DatabaseContract.BranchSettingTable.COL_DEPOSIT_AMOUNT, 5.0)
                put(DatabaseContract.BranchSettingTable.COL_DEPOSIT_REQUIRED, 1)
                put(DatabaseContract.BranchSettingTable.COL_CREATED_AT, nowUtc())
            }
            db.insert(DatabaseContract.BranchSettingTable.TABLE_NAME, null, values)
        }
    }

    private fun insertDemoOwner(db: SQLiteDatabase): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.OwnerTable.COL_DOC_TYPE, "DNI")
            put(DatabaseContract.OwnerTable.COL_DOC_NUMBER, "12345678")
            put(DatabaseContract.OwnerTable.COL_FULL_NAME, "Usuario Demo")
            put(DatabaseContract.OwnerTable.COL_PHONE, "987654321")
            put(DatabaseContract.OwnerTable.COL_EMAIL, "demo@mascotario.com")
            put(DatabaseContract.OwnerTable.COL_ADDRESS, "Lima, Perú")
            put(DatabaseContract.OwnerTable.COL_IS_ACTIVE, 1)
            put(DatabaseContract.OwnerTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.OwnerTable.TABLE_NAME, null, values)
    }

    private fun insertDemoUser(db: SQLiteDatabase, ownerId: Long): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.UserTable.COL_HOME_BRANCH_ID, 1)
            put(DatabaseContract.UserTable.COL_OWNER_ID, ownerId)
            put(DatabaseContract.UserTable.COL_FULL_NAME, "Usuario Demo")
            put(DatabaseContract.UserTable.COL_EMAIL, "demo@mascotario.com")
            put(DatabaseContract.UserTable.COL_PHONE, "987654321")
            put(DatabaseContract.UserTable.COL_PASSWORD_HASH, "123456")
            put(DatabaseContract.UserTable.COL_IS_ACTIVE, 1)
            put(DatabaseContract.UserTable.COL_CREATED_AT, nowUtc())
            put(DatabaseContract.UserTable.COL_LAST_LOGIN_AT, "")
        }
        return db.insert(DatabaseContract.UserTable.TABLE_NAME, null, values)
    }

    private fun insertSpecies(db: SQLiteDatabase) {
        val species = listOf("Perro", "Gato")
        for (name in species) {
            val values = ContentValues().apply {
                put(DatabaseContract.SpeciesTable.COL_NAME, name)
            }
            db.insert(DatabaseContract.SpeciesTable.TABLE_NAME, null, values)
        }
    }

    private fun insertBreeds(db: SQLiteDatabase) {
        insertBreed(db, 1, "Shih Tzu")
        insertBreed(db, 1, "Mestizo")
        insertBreed(db, 2, "Mestizo")
    }

    private fun insertBreed(db: SQLiteDatabase, speciesId: Int, name: String): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.BreedTable.COL_SPECIES_ID, speciesId)
            put(DatabaseContract.BreedTable.COL_NAME, name)
        }
        return db.insert(DatabaseContract.BreedTable.TABLE_NAME, null, values)
    }

    private fun insertAppChannel(db: SQLiteDatabase): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.ChannelTable.COL_CODE, DatabaseContract.Defaults.CHANNEL_APP)
            put(DatabaseContract.ChannelTable.COL_NAME, "App móvil")
        }
        return db.insert(DatabaseContract.ChannelTable.TABLE_NAME, null, values)
    }

    private fun insertPushNotificationChannel(db: SQLiteDatabase): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.NotificationChannelTable.COL_CODE, "PUSH")
            put(DatabaseContract.NotificationChannelTable.COL_NAME, "Push Notification")
        }
        return db.insert(DatabaseContract.NotificationChannelTable.TABLE_NAME, null, values)
    }

    private fun insertAppointmentType(
        db: SQLiteDatabase,
        code: String,
        name: String,
        durationMin: Int
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.AppointmentTypeTable.COL_CODE, code)
            put(DatabaseContract.AppointmentTypeTable.COL_NAME, name)
            put(DatabaseContract.AppointmentTypeTable.COL_DEFAULT_DURATION_MIN, durationMin)
        }
        return db.insert(DatabaseContract.AppointmentTypeTable.TABLE_NAME, null, values)
    }

    private fun insertService(
        db: SQLiteDatabase,
        code: String,
        name: String,
        category: String,
        basePrice: Double
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.ServiceCatalogTable.COL_CODE, code)
            put(DatabaseContract.ServiceCatalogTable.COL_NAME, name)
            put(DatabaseContract.ServiceCatalogTable.COL_CATEGORY, category)
            put(DatabaseContract.ServiceCatalogTable.COL_BASE_PRICE, basePrice)
            put(DatabaseContract.ServiceCatalogTable.COL_IS_ACTIVE, 1)
        }
        return db.insert(DatabaseContract.ServiceCatalogTable.TABLE_NAME, null, values)
    }

    private fun insertPet(
        db: SQLiteDatabase,
        ownerId: Long,
        name: String,
        speciesId: Long,
        breedId: Long?,
        sex: String,
        birthDate: String,
        color: String,
        notes: String,
        photoUrl: String
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.PetTable.COL_OWNER_ID, ownerId)
            put(DatabaseContract.PetTable.COL_NAME, name)
            put(DatabaseContract.PetTable.COL_SPECIES_ID, speciesId)
            if (breedId != null) put(DatabaseContract.PetTable.COL_BREED_ID, breedId)
            put(DatabaseContract.PetTable.COL_SEX, sex)
            put(DatabaseContract.PetTable.COL_BIRTH_DATE, birthDate)
            put(DatabaseContract.PetTable.COL_COLOR, color)
            put(DatabaseContract.PetTable.COL_NOTES, notes)
            put(DatabaseContract.PetTable.COL_PHOTO_URL, photoUrl)
            put(DatabaseContract.PetTable.COL_IS_ACTIVE, 1)
            put(DatabaseContract.PetTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.PetTable.TABLE_NAME, null, values)
    }

    private fun insertAppointment(
        db: SQLiteDatabase,
        branchId: Long,
        appointmentTypeId: Long,
        channelId: Long,
        ownerId: Long,
        petId: Long,
        startAt: String,
        endAt: String,
        status: String,
        notes: String
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.AppointmentTable.COL_BRANCH_ID, branchId)
            put(DatabaseContract.AppointmentTable.COL_APPOINTMENT_TYPE_ID, appointmentTypeId)
            put(DatabaseContract.AppointmentTable.COL_CHANNEL_ID, channelId)
            put(DatabaseContract.AppointmentTable.COL_OWNER_ID, ownerId)
            put(DatabaseContract.AppointmentTable.COL_PET_ID, petId)
            put(DatabaseContract.AppointmentTable.COL_START_AT, startAt)
            put(DatabaseContract.AppointmentTable.COL_END_AT, endAt)
            put(DatabaseContract.AppointmentTable.COL_STATUS, status)
            put(DatabaseContract.AppointmentTable.COL_NOTES, notes)
            put(DatabaseContract.AppointmentTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.AppointmentTable.TABLE_NAME, null, values)
    }

    private fun insertAppointmentService(
        db: SQLiteDatabase,
        appointmentId: Long,
        serviceId: Long,
        quantity: Int,
        unitPrice: Double,
        notes: String
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.AppointmentServiceTable.COL_APPOINTMENT_ID, appointmentId)
            put(DatabaseContract.AppointmentServiceTable.COL_SERVICE_ID, serviceId)
            put(DatabaseContract.AppointmentServiceTable.COL_QUANTITY, quantity)
            put(DatabaseContract.AppointmentServiceTable.COL_UNIT_PRICE, unitPrice)
            put(DatabaseContract.AppointmentServiceTable.COL_NOTES, notes)
        }
        return db.insert(DatabaseContract.AppointmentServiceTable.TABLE_NAME, null, values)
    }

    private fun insertClinicalVisit(
        db: SQLiteDatabase,
        branchId: Long,
        appointmentId: Long,
        ownerId: Long,
        petId: Long,
        vetUserId: Long?,
        summaryForClient: String,
        notesForClient: String,
        notesPrivate: String
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.ClinicalVisitTable.COL_BRANCH_ID, branchId)
            put(DatabaseContract.ClinicalVisitTable.COL_APPOINTMENT_ID, appointmentId)
            put(DatabaseContract.ClinicalVisitTable.COL_OWNER_ID, ownerId)
            put(DatabaseContract.ClinicalVisitTable.COL_PET_ID, petId)
            if (vetUserId != null) put(DatabaseContract.ClinicalVisitTable.COL_VET_USER_ID, vetUserId)
            put(DatabaseContract.ClinicalVisitTable.COL_VISIT_AT, nowUtc())
            put(DatabaseContract.ClinicalVisitTable.COL_SUMMARY_FOR_CLIENT, summaryForClient)
            put(DatabaseContract.ClinicalVisitTable.COL_NOTES_FOR_CLIENT, notesForClient)
            put(DatabaseContract.ClinicalVisitTable.COL_NOTES_PRIVATE, notesPrivate)
            put(DatabaseContract.ClinicalVisitTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.ClinicalVisitTable.TABLE_NAME, null, values)
    }

    private fun insertPayment(
        db: SQLiteDatabase,
        branchId: Long,
        appointmentId: Long,
        ownerId: Long,
        method: String,
        status: String,
        amount: Double,
        paidAt: String,
        registeredByUserId: Long?,
        validatedByUserId: Long?
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.PaymentTable.COL_BRANCH_ID, branchId)
            put(DatabaseContract.PaymentTable.COL_APPOINTMENT_ID, appointmentId)
            put(DatabaseContract.PaymentTable.COL_OWNER_ID, ownerId)
            put(DatabaseContract.PaymentTable.COL_METHOD, method)
            put(DatabaseContract.PaymentTable.COL_STATUS, status)
            put(DatabaseContract.PaymentTable.COL_AMOUNT, amount)
            put(DatabaseContract.PaymentTable.COL_PAID_AT, paidAt)
            if (registeredByUserId != null) put(DatabaseContract.PaymentTable.COL_REGISTERED_BY_USER_ID, registeredByUserId)
            put(DatabaseContract.PaymentTable.COL_REGISTERED_AT, nowUtc())
            if (validatedByUserId != null) put(DatabaseContract.PaymentTable.COL_VALIDATED_BY_USER_ID, validatedByUserId)
            put(DatabaseContract.PaymentTable.COL_VALIDATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.PaymentTable.TABLE_NAME, null, values)
    }

    private fun insertPaymentItem(
        db: SQLiteDatabase,
        paymentId: Long,
        appointmentServiceId: Long?,
        conceptName: String,
        amount: Double
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.PaymentItemTable.COL_PAYMENT_ID, paymentId)
            if (appointmentServiceId != null) {
                put(DatabaseContract.PaymentItemTable.COL_APPOINTMENT_SERVICE_ID, appointmentServiceId)
            }
            put(DatabaseContract.PaymentItemTable.COL_CONCEPT_NAME, conceptName)
            put(DatabaseContract.PaymentItemTable.COL_AMOUNT, amount)
        }
        return db.insert(DatabaseContract.PaymentItemTable.TABLE_NAME, null, values)
    }

    private fun insertPaymentEvidence(
        db: SQLiteDatabase,
        paymentId: Long,
        fileUrl: String,
        fileName: String,
        contentType: String,
        uploadedByUserId: Long?
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.PaymentEvidenceTable.COL_PAYMENT_ID, paymentId)
            put(DatabaseContract.PaymentEvidenceTable.COL_FILE_URL, fileUrl)
            put(DatabaseContract.PaymentEvidenceTable.COL_FILE_NAME, fileName)
            put(DatabaseContract.PaymentEvidenceTable.COL_CONTENT_TYPE, contentType)
            put(DatabaseContract.PaymentEvidenceTable.COL_UPLOADED_AT, nowUtc())
            if (uploadedByUserId != null) {
                put(DatabaseContract.PaymentEvidenceTable.COL_UPLOADED_BY_USER_ID, uploadedByUserId)
            }
        }
        return db.insert(DatabaseContract.PaymentEvidenceTable.TABLE_NAME, null, values)
    }

    private fun insertNotificationType(
        db: SQLiteDatabase,
        code: String,
        name: String,
        isMarketing: Int,
        isActive: Int
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.NotificationTypeTable.COL_CODE, code)
            put(DatabaseContract.NotificationTypeTable.COL_NAME, name)
            put(DatabaseContract.NotificationTypeTable.COL_IS_MARKETING, isMarketing)
            put(DatabaseContract.NotificationTypeTable.COL_IS_ACTIVE, isActive)
        }
        return db.insert(DatabaseContract.NotificationTypeTable.TABLE_NAME, null, values)
    }

    private fun insertNotificationPreference(
        db: SQLiteDatabase,
        userId: Long,
        notificationTypeId: Long,
        notificationChannelId: Long,
        isEnabled: Int
    ): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.NotificationPreferenceTable.COL_USER_ID, userId)
            put(DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_TYPE_ID, notificationTypeId)
            put(DatabaseContract.NotificationPreferenceTable.COL_NOTIFICATION_CHANNEL_ID, notificationChannelId)
            put(DatabaseContract.NotificationPreferenceTable.COL_IS_ENABLED, isEnabled)
            put(DatabaseContract.NotificationPreferenceTable.COL_CREATED_AT, nowUtc())
        }
        return db.insert(DatabaseContract.NotificationPreferenceTable.TABLE_NAME, null, values)
    }

    private fun insertNotification(
        db: SQLiteDatabase,
        userId: Long?,
        ownerId: Long?,
        appointmentId: Long?,
        paymentId: Long?,
        visitId: Long?,
        notificationTypeId: Long,
        notificationChannelId: Long,
        title: String,
        message: String,
        destinationAddress: String?,
        status: String
    ): Long {
        val values = ContentValues().apply {
            if (userId != null) put(DatabaseContract.NotificationTable.COL_USER_ID, userId)
            if (ownerId != null) put(DatabaseContract.NotificationTable.COL_OWNER_ID, ownerId)
            if (appointmentId != null) put(DatabaseContract.NotificationTable.COL_APPOINTMENT_ID, appointmentId)
            if (paymentId != null) put(DatabaseContract.NotificationTable.COL_PAYMENT_ID, paymentId)
            if (visitId != null) put(DatabaseContract.NotificationTable.COL_VISIT_ID, visitId)

            put(DatabaseContract.NotificationTable.COL_NOTIFICATION_TYPE_ID, notificationTypeId)
            put(DatabaseContract.NotificationTable.COL_NOTIFICATION_CHANNEL_ID, notificationChannelId)
            put(DatabaseContract.NotificationTable.COL_TITLE, title)
            put(DatabaseContract.NotificationTable.COL_MESSAGE, message)
            put(DatabaseContract.NotificationTable.COL_DESTINATION_ADDRESS, destinationAddress)
            put(DatabaseContract.NotificationTable.COL_STATUS, status)
            put(DatabaseContract.NotificationTable.COL_SCHEDULED_AT, nowUtc())
            put(DatabaseContract.NotificationTable.COL_CREATED_AT, nowUtc())

            when (status) {
                DatabaseContract.Defaults.NOTIFICATION_STATUS_SENT ->
                    put(DatabaseContract.NotificationTable.COL_SENT_AT, nowUtc())

                DatabaseContract.Defaults.NOTIFICATION_STATUS_DELIVERED -> {
                    put(DatabaseContract.NotificationTable.COL_SENT_AT, nowUtc())
                    put(DatabaseContract.NotificationTable.COL_DELIVERED_AT, nowUtc())
                }

                DatabaseContract.Defaults.NOTIFICATION_STATUS_READ -> {
                    put(DatabaseContract.NotificationTable.COL_SENT_AT, nowUtc())
                    put(DatabaseContract.NotificationTable.COL_DELIVERED_AT, nowUtc())
                    put(DatabaseContract.NotificationTable.COL_READ_AT, nowUtc())
                }
            }
        }
        return db.insert(DatabaseContract.NotificationTable.TABLE_NAME, null, values)
    }

    /**
     * Función simple para devolver la fecha/hora actual como String.
     * SQLite no maneja datetime2 como SQL Server, así que en este MVP
     * lo guardaremos como texto ISO-like simple.
     */
    private fun nowUtc(): String {
        return java.time.LocalDateTime.now().toString()
    }
}