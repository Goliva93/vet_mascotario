package pe.goliva.vet_mascotario.ui.appointment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.AppointmentDao
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.utils.SessionManager
import java.util.Calendar
import java.util.Locale

class AppointmentCreateActivity : AppCompatActivity() {

    private var currentStep = 1
    private val totalSteps = 5

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao
    private lateinit var appointmentDao: AppointmentDao
    private lateinit var userProfileDao: UserProfileDao

    private lateinit var stepContainer: FrameLayout
    private lateinit var btnContinue: MaterialButton
    private lateinit var btnCancelBottom: MaterialButton
    private lateinit var btnBackTop: ImageButton
    private lateinit var tvStepIndicator: TextView
    private lateinit var tvStepSubtitle: TextView

    private lateinit var step1Circle: TextView
    private lateinit var step2Circle: TextView
    private lateinit var step3Circle: TextView
    private lateinit var step4Circle: TextView
    private lateinit var step5Circle: TextView

    private lateinit var line1_2: View
    private lateinit var line2_3: View
    private lateinit var line3_4: View
    private lateinit var line4_5: View

    private var petsAdapter: AppointmentPetSelectionAdapter? = null
    private var appointmentTypeAdapter: AppointmentTypeSelectionAdapter? = null
    private var branchAdapter: AppointmentBranchSelectionAdapter? = null
    private var timeSlotAdapter: AppointmentTimeSlotAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppointmentDraftStore.clear()

        setContentView(R.layout.activity_appointment_create)

        sessionManager = SessionManager(this)
        petDao = PetDao(this)
        appointmentDao = AppointmentDao(this)
        userProfileDao = UserProfileDao(this)

        bindViews()
        setupClicks()
        renderStep()
    }

    private fun bindViews() {
        stepContainer = findViewById(R.id.step_container)
        btnContinue = findViewById(R.id.btn_continue)
        btnCancelBottom = findViewById(R.id.btn_cancel_bottom)
        btnBackTop = findViewById(R.id.btn_back_top)
        tvStepIndicator = findViewById(R.id.tv_step_indicator)
        tvStepSubtitle = findViewById(R.id.tv_step_subtitle)

        step1Circle = findViewById(R.id.step1_circle)
        step2Circle = findViewById(R.id.step2_circle)
        step3Circle = findViewById(R.id.step3_circle)
        step4Circle = findViewById(R.id.step4_circle)
        step5Circle = findViewById(R.id.step5_circle)

        line1_2 = findViewById(R.id.line_1_2)
        line2_3 = findViewById(R.id.line_2_3)
        line3_4 = findViewById(R.id.line_3_4)
        line4_5 = findViewById(R.id.line_4_5)
    }

    private fun setupClicks() {
        btnContinue.setOnClickListener {
            if (!validateCurrentStep()) return@setOnClickListener

            if (currentStep < totalSteps) {
                if (currentStep == 5) return@setOnClickListener
                currentStep++
                renderStep()
            } else {
                saveAppointment()
            }
        }

        btnCancelBottom.setOnClickListener {
            handleBackAction()
        }

        btnBackTop.setOnClickListener {
            handleBackAction()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackAction()
    }

    private fun handleBackAction() {
        if (currentStep > 1) {
            currentStep--
            renderStep()
        } else {
            AppointmentDraftStore.clear()
            finish()
        }
    }

    private fun renderStep() {
        tvStepIndicator.text = "Paso $currentStep de $totalSteps"
        tvStepSubtitle.text = getStepSubtitle(currentStep)

        btnContinue.text = if (currentStep == totalSteps) {
            "Confirmar cita"
        } else {
            "Continuar"
        }

        btnCancelBottom.text = if (currentStep == 1) {
            "Cancelar"
        } else {
            "Volver"
        }

        stepContainer.removeAllViews()
        val stepLayout = when (currentStep) {
            1 -> R.layout.layout_step1_select_pet
            2 -> R.layout.layout_step2_select_service
            3 -> R.layout.layout_step3_select_branch
            4 -> R.layout.layout_step4_select_datetime
            5 -> R.layout.layout_step5_confirm
            else -> R.layout.layout_step1_select_pet
        }

        layoutInflater.inflate(stepLayout, stepContainer, true)

        when (currentStep) {
            1 -> setupStepOne()
            2 -> setupStepTwo()
            3 -> setupStepThree()
            4 -> setupStepFour()
            5 -> setupStepFive()
        }

        paintStepper()
    }

    private fun setupStepOne() {
        val rvPets = findViewById<RecyclerView>(R.id.rv_step1_pets)
        val emptyLayout = findViewById<LinearLayout>(R.id.layout_step1_empty)

        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            rvPets.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            btnContinue.isEnabled = false
            return
        }

        val pets = petDao.getPetsByUserId(userId)

        if (pets.isEmpty()) {
            rvPets.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            btnContinue.isEnabled = false
            return
        }

        rvPets.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE

        petsAdapter = AppointmentPetSelectionAdapter(
            items = pets,
            selectedPetId = AppointmentDraftStore.petId
        ) { pet ->
            AppointmentDraftStore.petId = pet.petId
            AppointmentDraftStore.petName = pet.name
            btnContinue.isEnabled = true
        }

        rvPets.adapter = petsAdapter
        btnContinue.isEnabled = AppointmentDraftStore.petId != null
    }

    private fun setupStepTwo() {
        val tvSelectedPetContext = findViewById<TextView>(R.id.tv_selected_pet_context)
        val rvServices = findViewById<RecyclerView>(R.id.rv_step2_services)

        tvSelectedPetContext.text =
            "Mascota seleccionada: ${AppointmentDraftStore.petName.ifBlank { "No seleccionada" }}"

        val types = appointmentDao.getAppointmentTypes()

        appointmentTypeAdapter = AppointmentTypeSelectionAdapter(
            items = types,
            selectedTypeId = AppointmentDraftStore.appointmentTypeId
        ) { type ->
            AppointmentDraftStore.appointmentTypeId = type.appointmentTypeId
            AppointmentDraftStore.appointmentTypeName = type.name
            AppointmentDraftStore.appointmentDurationMin = type.defaultDurationMin
            btnContinue.isEnabled = true
        }

        rvServices.adapter = appointmentTypeAdapter
        btnContinue.isEnabled = AppointmentDraftStore.appointmentTypeId != null
    }

    private fun setupStepThree() {
        val tvSelectedServiceContext = findViewById<TextView>(R.id.tv_selected_service_context)
        val rvBranches = findViewById<RecyclerView>(R.id.rv_step3_branches)

        tvSelectedServiceContext.text =
            "Servicio seleccionado: ${AppointmentDraftStore.appointmentTypeName.ifBlank { "No seleccionado" }}"

        val branches = userProfileDao.getActiveBranches()
        val userId = sessionManager.getUserId()

        val preferredBranchId = userProfileDao.getUserProfileById(userId)?.homeBranchId

        if (AppointmentDraftStore.branchId == null && preferredBranchId != null) {
            val preferredBranch = branches.firstOrNull { it.branchId == preferredBranchId }
            if (preferredBranch != null) {
                AppointmentDraftStore.branchId = preferredBranch.branchId
                AppointmentDraftStore.branchName = preferredBranch.name
            }
        }

        branchAdapter = AppointmentBranchSelectionAdapter(
            items = branches,
            selectedBranchId = AppointmentDraftStore.branchId
        ) { branch ->
            AppointmentDraftStore.branchId = branch.branchId
            AppointmentDraftStore.branchName = branch.name
            btnContinue.isEnabled = true
        }

        rvBranches.adapter = branchAdapter
        btnContinue.isEnabled = AppointmentDraftStore.branchId != null
    }

    private fun setupStepFour() {
        val tvSelectedBranchContext = findViewById<TextView>(R.id.tv_selected_branch_context)
        val etSelectedDate = findViewById<TextInputEditText>(R.id.et_selected_date)
        val rvTimeSlots = findViewById<RecyclerView>(R.id.rv_step4_time_slots)
        val tvEmptySlots = findViewById<TextView>(R.id.tv_step4_empty_slots)

        tvSelectedBranchContext.text =
            "Sede seleccionada: ${AppointmentDraftStore.branchName.ifBlank { "No seleccionada" }}"

        etSelectedDate.setText(AppointmentDraftStore.selectedDate)

        etSelectedDate.setOnClickListener {
            showDatePicker { selectedDate ->
                AppointmentDraftStore.selectedDate = selectedDate
                AppointmentDraftStore.selectedTime = ""
                AppointmentDraftStore.selectedEndTime = ""
                AppointmentDraftStore.selectedStartAtDb = ""
                AppointmentDraftStore.selectedEndAtDb = ""

                etSelectedDate.setText(selectedDate)
                loadTimeSlots(rvTimeSlots, tvEmptySlots)
            }
        }

        loadTimeSlots(rvTimeSlots, tvEmptySlots)
    }

    private fun loadTimeSlots(
        rvTimeSlots: RecyclerView,
        tvEmptySlots: TextView
    ) {
        val branchId = AppointmentDraftStore.branchId
        val selectedDate = AppointmentDraftStore.selectedDate
        val durationMin = AppointmentDraftStore.appointmentDurationMin

        if (branchId == null || selectedDate.isBlank() || durationMin == null) {
            rvTimeSlots.visibility = View.GONE
            tvEmptySlots.visibility = View.VISIBLE
            tvEmptySlots.text = "Selecciona una fecha para ver horarios"
            btnContinue.isEnabled = false
            return
        }

        val slots = appointmentDao.getAvailableTimeSlots(
            branchId = branchId,
            selectedDate = selectedDate,
            durationMin = durationMin
        )

        if (slots.isEmpty()) {
            rvTimeSlots.visibility = View.GONE
            tvEmptySlots.visibility = View.VISIBLE
            tvEmptySlots.text = "No hay horarios configurados para esa fecha"
            btnContinue.isEnabled = false
            return
        }

        rvTimeSlots.visibility = View.VISIBLE
        tvEmptySlots.visibility = View.VISIBLE
        tvEmptySlots.text = "Selecciona un horario disponible"

        timeSlotAdapter = AppointmentTimeSlotAdapter(
            items = slots,
            selectedStartAtDb = AppointmentDraftStore.selectedStartAtDb
        ) { slot ->
            AppointmentDraftStore.selectedTime = slot.startTime
            AppointmentDraftStore.selectedEndTime = slot.endTime
            AppointmentDraftStore.selectedStartAtDb = slot.startAtDb
            AppointmentDraftStore.selectedEndAtDb = slot.endAtDb
            btnContinue.isEnabled = true
        }

        rvTimeSlots.adapter = timeSlotAdapter
        btnContinue.isEnabled = AppointmentDraftStore.selectedStartAtDb.isNotBlank()
    }

    private fun setupStepFive() {
        val tvConfirmPet = findViewById<TextView>(R.id.tv_confirm_pet)
        val tvConfirmService = findViewById<TextView>(R.id.tv_confirm_service)
        val tvConfirmBranch = findViewById<TextView>(R.id.tv_confirm_branch)
        val tvConfirmDateTime = findViewById<TextView>(R.id.tv_confirm_date_time)
        val etConfirmNotes = findViewById<TextInputEditText>(R.id.et_confirm_notes)

        tvConfirmPet.text = "Mascota: ${AppointmentDraftStore.petName}"
        tvConfirmService.text = "Servicio: ${AppointmentDraftStore.appointmentTypeName}"
        tvConfirmBranch.text = "Sede: ${AppointmentDraftStore.branchName}"
        tvConfirmDateTime.text =
            "Fecha y hora: ${AppointmentDraftStore.selectedDate} · ${AppointmentDraftStore.selectedTime} - ${AppointmentDraftStore.selectedEndTime}"

        etConfirmNotes.setText(AppointmentDraftStore.notes)
        etConfirmNotes.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                AppointmentDraftStore.notes = etConfirmNotes.text?.toString()?.trim().orEmpty()
            }
        }

        btnContinue.isEnabled = true
    }

    private fun saveAppointment() {
        AppointmentDraftStore.notes = findViewById<TextInputEditText>(R.id.et_confirm_notes)
            ?.text?.toString()?.trim().orEmpty()

        val userId = sessionManager.getUserId()
        val petId = AppointmentDraftStore.petId
        val appointmentTypeId = AppointmentDraftStore.appointmentTypeId
        val branchId = AppointmentDraftStore.branchId
        val startAt = AppointmentDraftStore.selectedStartAtDb
        val endAt = AppointmentDraftStore.selectedEndAtDb

        if (userId == -1L || petId == null || appointmentTypeId == null || branchId == null ||
            startAt.isBlank() || endAt.isBlank()
        ) {
            Toast.makeText(
                this,
                "Faltan datos para registrar la cita",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        btnContinue.isEnabled = false

        val created = appointmentDao.createAppointmentForUser(
            userId = userId,
            petId = petId,
            appointmentTypeId = appointmentTypeId,
            branchId = branchId,
            startAt = startAt,
            endAt = endAt,
            notes = AppointmentDraftStore.notes.takeIf { it.isNotBlank() }
        )

        btnContinue.isEnabled = true

        if (created) {
            Toast.makeText(
                this,
                "Cita registrada correctamente",
                Toast.LENGTH_SHORT
            ).show()
            AppointmentDraftStore.clear()
            finish()
        } else {
            Toast.makeText(
                this,
                "No se pudo registrar la cita",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> {
                if (AppointmentDraftStore.petId == null) {
                    Toast.makeText(
                        this,
                        "Selecciona una mascota para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else true
            }
            2 -> {
                if (AppointmentDraftStore.appointmentTypeId == null) {
                    Toast.makeText(
                        this,
                        "Selecciona un servicio para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else true
            }
            3 -> {
                if (AppointmentDraftStore.branchId == null) {
                    Toast.makeText(
                        this,
                        "Selecciona una sede para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else true
            }
            4 -> {
                if (AppointmentDraftStore.selectedDate.isBlank() ||
                    AppointmentDraftStore.selectedStartAtDb.isBlank()
                ) {
                    Toast.makeText(
                        this,
                        "Selecciona fecha y horario para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else true
            }
            else -> true
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun getStepSubtitle(step: Int): String {
        return when (step) {
            1 -> "Selecciona la mascota"
            2 -> "Selecciona el servicio"
            3 -> "Selecciona la sede"
            4 -> "Selecciona fecha y hora"
            5 -> "Revisa y confirma"
            else -> ""
        }
    }

    private fun paintStepper() {
        val activeBg = ContextCompat.getDrawable(this, R.drawable.bg_stepper_active)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_stepper_inactive)

        val textWhite = ContextCompat.getColor(this, R.color.bg_white)
        val textGray = ContextCompat.getColor(this, R.color.text_gray)
        val lineActive = ContextCompat.getColor(this, R.color.green_primary)
        val lineInactive = ContextCompat.getColor(this, R.color.gray_card_bg)

        val circles = listOf(step1Circle, step2Circle, step3Circle, step4Circle, step5Circle)
        val lines = listOf(line1_2, line2_3, line3_4, line4_5)

        circles.forEach {
            it.background = inactiveBg
            it.setTextColor(textGray)
        }

        lines.forEach {
            it.setBackgroundColor(lineInactive)
        }

        for (i in 0 until currentStep) {
            circles[i].background = activeBg
            circles[i].setTextColor(textWhite)
        }

        for (i in 0 until (currentStep - 1).coerceAtLeast(0)) {
            lines[i].setBackgroundColor(lineActive)
        }

        btnContinue.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.green_primary)
    }
}