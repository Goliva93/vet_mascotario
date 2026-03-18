package pe.goliva.vet_mascotario.data.db

/**
 * esta clase centraliza las tablas y columnas de la base de datos
 * en si un diccionario de la base de datos
 */
object DatabaseContract {

    // Nombre del archivo SQLite que se creará en el dispositivo
    const val DATABASE_NAME = "vet_mascotario.db"

    // Versión de la BD.
    // Cuando cambies estructura de tablas en el futuro, esta versión deberá subir.
    const val DATABASE_VERSION = 1

    /**
     * Aquí dejamos algunas constantes útiles para estados y códigos.
     * No son obligatorias, pero ayudan a evitar hardcodear valores repetidos.
     */
    object Defaults {
        const val CHANNEL_APP = "APP"

        const val APPOINTMENT_TYPE_VET = "VET"
        const val APPOINTMENT_TYPE_GROOMING = "GROOMING"
        const val APPOINTMENT_TYPE_CONTROL = "CONTROL"

        const val APPOINTMENT_STATUS_CONFIRMED = "CONFIRMED"
        const val APPOINTMENT_STATUS_PENDING = "PENDING"
        const val APPOINTMENT_STATUS_CANCELLED = "CANCELLED"
        const val APPOINTMENT_STATUS_FINISHED = "FINISHED"

        const val PAYMENT_STATUS_REGISTERED = "REGISTERED"
        const val PAYMENT_STATUS_VALIDATED = "VALIDATED"
        const val PAYMENT_STATUS_VOIDED = "VOIDED"

        const val NOTIFICATION_STATUS_PENDING = "PENDING"
        const val NOTIFICATION_STATUS_SENT = "SENT"
        const val NOTIFICATION_STATUS_DELIVERED = "DELIVERED"
        const val NOTIFICATION_STATUS_READ = "READ"
        const val NOTIFICATION_STATUS_FAILED = "FAILED"
    }

    /**
     * Tabla Branch
     * Representa las sedes de la veterinaria.
     */
    object BranchTable {
        const val TABLE_NAME = "Branch"

        const val COL_BRANCH_ID = "BranchId"
        const val COL_NAME = "Name"
        const val COL_ADDRESS = "Address"
        const val COL_PHONE = "Phone"
        const val COL_IS_ACTIVE = "IsActive"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla BranchSetting
     * Configuración específica de cada sede.
     */
    object BranchSettingTable {
        const val TABLE_NAME = "BranchSetting"

        const val COL_BRANCH_ID = "BranchId"
        const val COL_CANCEL_CUTOFF_RULE = "CancelCutoffRule"
        const val COL_DEPOSIT_AMOUNT = "DepositAmount"
        const val COL_DEPOSIT_REQUIRED = "DepositRequired"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla Owner
     * Representa al dueño/cliente de la mascota.
     */
    object OwnerTable {
        const val TABLE_NAME = "Owner"

        const val COL_OWNER_ID = "OwnerId"
        const val COL_DOC_TYPE = "DocType"
        const val COL_DOC_NUMBER = "DocNumber"
        const val COL_FULL_NAME = "FullName"
        const val COL_PHONE = "Phone"
        const val COL_EMAIL = "Email"
        const val COL_ADDRESS = "Address"
        const val COL_IS_ACTIVE = "IsActive"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla User
     * Usuario de la app o del sistema.
     *
     * En tu modelo real, User puede estar relacionado con Owner.
     * Para el cliente móvil, normalmente crearemos primero Owner y luego User.
     */
    object UserTable {
        const val TABLE_NAME = "User"

        const val COL_USER_ID = "UserId"
        const val COL_HOME_BRANCH_ID = "HomeBranchId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_FULL_NAME = "FullName"
        const val COL_EMAIL = "Email"
        const val COL_PHONE = "Phone"
        const val COL_PASSWORD_HASH = "PasswordHash"
        const val COL_IS_ACTIVE = "IsActive"
        const val COL_CREATED_AT = "CreatedAt"
        const val COL_LAST_LOGIN_AT = "LastLoginAt"

        // NOTA:
        // RowVer existe en SQL Server, pero no en SQLite.
        // Por eso NO lo declaramos aquí.
    }

    /**
     * Tabla Species
     * Catálogo de especies: Perro, Gato, etc.
     */
    object SpeciesTable {
        const val TABLE_NAME = "Species"

        const val COL_SPECIES_ID = "SpeciesId"
        const val COL_NAME = "Name"
    }

    /**
     * Tabla Breed
     * Catálogo de razas relacionadas a una especie.
     */
    object BreedTable {
        const val TABLE_NAME = "Breed"

        const val COL_BREED_ID = "BreedId"
        const val COL_SPECIES_ID = "SpeciesId"
        const val COL_NAME = "Name"
    }

    /**
     * Tabla Channel
     * Canal por donde se originó una cita:
     * APP, WEB, COUNTER, WHATSAPP, etc.
     */
    object ChannelTable {
        const val TABLE_NAME = "Channel"

        const val COL_CHANNEL_ID = "ChannelId"
        const val COL_CODE = "Code"
        const val COL_NAME = "Name"
    }

    /**
     * Tabla AppointmentType
     */
    object AppointmentTypeTable {
        const val TABLE_NAME = "AppointmentType"

        const val COL_APPOINTMENT_TYPE_ID = "AppointmentTypeId"
        const val COL_CODE = "Code"
        const val COL_NAME = "Name"
        const val COL_DEFAULT_DURATION_MIN = "DefaultDurationMin"
    }

    /**
     * Tabla ServiceCatalog
     */
    object ServiceCatalogTable {
        const val TABLE_NAME = "ServiceCatalog"

        const val COL_SERVICE_ID = "ServiceId"
        const val COL_CODE = "Code"
        const val COL_NAME = "Name"
        const val COL_CATEGORY = "Category"
        const val COL_BASE_PRICE = "BasePrice"
        const val COL_IS_ACTIVE = "IsActive"
    }

    /**
     * Tabla Pet
     */
    object PetTable {
        const val TABLE_NAME = "Pet"

        const val COL_PET_ID = "PetId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_NAME = "Name"
        const val COL_SPECIES_ID = "SpeciesId"
        const val COL_BREED_ID = "BreedId"
        const val COL_SEX = "Sex"
        const val COL_BIRTH_DATE = "BirthDate"
        const val COL_COLOR = "Color"
        const val COL_NOTES = "Notes"
        const val COL_PHOTO_URL = "PhotoUrl"
        const val COL_IS_ACTIVE = "IsActive"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla Appointment
     * Citas de la app
     */
    object AppointmentTable {
        const val TABLE_NAME = "Appointment"

        const val COL_APPOINTMENT_ID = "AppointmentId"
        const val COL_BRANCH_ID = "BranchId"
        const val COL_APPOINTMENT_TYPE_ID = "AppointmentTypeId"
        const val COL_CHANNEL_ID = "ChannelId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_PET_ID = "PetId"
        const val COL_ASSIGNED_VET_USER_ID = "AssignedVetUserId"
        const val COL_ASSIGNED_GROOMER_USER_ID = "AssignedGroomerUserId"
        const val COL_START_AT = "StartAt"
        const val COL_END_AT = "EndAt"
        const val COL_STATUS = "Status"
        const val COL_NOTES = "Notes"
        const val COL_RESCHEDULED_FROM_APPOINTMENT_ID = "RescheduledFromAppointmentId"
        const val COL_CANCEL_REASON = "CancelReason"
        const val COL_CANCELLED_AT = "CancelledAt"
        const val COL_CANCELLED_BY_USER_ID = "CancelledByUserId"
        const val COL_CREATED_BY_USER_ID = "CreatedByUserId"
        const val COL_CREATED_AT = "CreatedAt"
        const val COL_UPDATED_AT = "UpdatedAt"
    }

    /**
     * Tabla AppointmentService
     */
    object AppointmentServiceTable {
        const val TABLE_NAME = "AppointmentService"

        const val COL_APPOINTMENT_SERVICE_ID = "AppointmentServiceId"
        const val COL_APPOINTMENT_ID = "AppointmentId"
        const val COL_SERVICE_ID = "ServiceId"
        const val COL_QUANTITY = "Quantity"
        const val COL_UNIT_PRICE = "UnitPrice"
        const val COL_NOTES = "Notes"
    }

    /**
     * Tabla ClinicalVisit
     */
    object ClinicalVisitTable {
        const val TABLE_NAME = "ClinicalVisit"

        const val COL_VISIT_ID = "VisitId"
        const val COL_BRANCH_ID = "BranchId"
        const val COL_APPOINTMENT_ID = "AppointmentId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_PET_ID = "PetId"
        const val COL_VET_USER_ID = "VetUserId"
        const val COL_VISIT_AT = "VisitAt"
        const val COL_NOTES_PRIVATE = "NotesPrivate"
        const val COL_SUMMARY_FOR_CLIENT = "SummaryForClient"
        const val COL_NOTES_FOR_CLIENT = "NotesForClient"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla Payment
     */
    object PaymentTable {
        const val TABLE_NAME = "Payment"

        const val COL_PAYMENT_ID = "PaymentId"
        const val COL_BRANCH_ID = "BranchId"
        const val COL_APPOINTMENT_ID = "AppointmentId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_METHOD = "Method"
        const val COL_STATUS = "Status"
        const val COL_AMOUNT = "Amount"
        const val COL_PAID_AT = "PaidAt"
        const val COL_REGISTERED_BY_USER_ID = "RegisteredByUserId"
        const val COL_REGISTERED_AT = "RegisteredAt"
        const val COL_VALIDATED_BY_USER_ID = "ValidatedByUserId"
        const val COL_VALIDATED_AT = "ValidatedAt"
        const val COL_VOIDED_BY_USER_ID = "VoidedByUserId"
        const val COL_VOIDED_AT = "VoidedAt"
        const val COL_VOID_REASON = "VoidReason"
    }

    /**
     * Tabla PaymentItem
     */
    object PaymentItemTable {
        const val TABLE_NAME = "PaymentItem"

        const val COL_PAYMENT_ITEM_ID = "PaymentItemId"
        const val COL_PAYMENT_ID = "PaymentId"
        const val COL_APPOINTMENT_SERVICE_ID = "AppointmentServiceId"
        const val COL_CONCEPT_NAME = "ConceptName"
        const val COL_AMOUNT = "Amount"
    }

    /**
     * Tabla PaymentEvidence
     */
    object PaymentEvidenceTable {
        const val TABLE_NAME = "PaymentEvidence"

        const val COL_EVIDENCE_ID = "EvidenceId"
        const val COL_PAYMENT_ID = "PaymentId"
        const val COL_FILE_URL = "FileUrl"
        const val COL_FILE_NAME = "FileName"
        const val COL_CONTENT_TYPE = "ContentType"
        const val COL_UPLOADED_AT = "UploadedAt"
        const val COL_UPLOADED_BY_USER_ID = "UploadedByUserId"
    }

    /**
     * Tabla NotificationChannel
     */
    object NotificationChannelTable {
        const val TABLE_NAME = "NotificationChannel"

        const val COL_NOTIFICATION_CHANNEL_ID = "NotificationChannelId"
        const val COL_CODE = "Code"
        const val COL_NAME = "Name"
    }

    /**
     * Tabla NotificationType
     */
    object NotificationTypeTable {
        const val TABLE_NAME = "NotificationType"

        const val COL_NOTIFICATION_TYPE_ID = "NotificationTypeId"
        const val COL_CODE = "Code"
        const val COL_NAME = "Name"
        const val COL_IS_MARKETING = "IsMarketing"
        const val COL_IS_ACTIVE = "IsActive"
    }

    /**
     * Tabla NotificationPreference
     */
    object NotificationPreferenceTable {
        const val TABLE_NAME = "NotificationPreference"

        const val COL_NOTIFICATION_PREFERENCE_ID = "NotificationPreferenceId"
        const val COL_USER_ID = "UserId"
        const val COL_NOTIFICATION_TYPE_ID = "NotificationTypeId"
        const val COL_NOTIFICATION_CHANNEL_ID = "NotificationChannelId"
        const val COL_IS_ENABLED = "IsEnabled"
        const val COL_CREATED_AT = "CreatedAt"
    }

    /**
     * Tabla Notification
     */
    object NotificationTable {
        const val TABLE_NAME = "Notification"

        const val COL_NOTIFICATION_ID = "NotificationId"
        const val COL_USER_ID = "UserId"
        const val COL_OWNER_ID = "OwnerId"
        const val COL_APPOINTMENT_ID = "AppointmentId"
        const val COL_PAYMENT_ID = "PaymentId"
        const val COL_VISIT_ID = "VisitId"
        const val COL_NOTIFICATION_TYPE_ID = "NotificationTypeId"
        const val COL_NOTIFICATION_CHANNEL_ID = "NotificationChannelId"
        const val COL_TITLE = "Title"
        const val COL_MESSAGE = "Message"
        const val COL_DESTINATION_ADDRESS = "DestinationAddress"
        const val COL_STATUS = "Status"
        const val COL_SCHEDULED_AT = "ScheduledAt"
        const val COL_SENT_AT = "SentAt"
        const val COL_DELIVERED_AT = "DeliveredAt"
        const val COL_READ_AT = "ReadAt"
        const val COL_FAILED_AT = "FailedAt"
        const val COL_ERROR_MESSAGE = "ErrorMessage"
        const val COL_EXTERNAL_MESSAGE_ID = "ExternalMessageId"
        const val COL_CREATED_BY_USER_ID = "CreatedByUserId"
        const val COL_CREATED_AT = "CreatedAt"
    }
}