package pe.goliva.vet_mascotario.ui.appointment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import pe.goliva.vet_mascotario.R

class AppointmentCreateActivity : AppCompatActivity() {

    // variable para alacenar el paso en el que se encuentra
    private var pasoActual = 1

    // se declara las vistas a modificar
    private lateinit var contenedorPasos: FrameLayout
    private lateinit var btnContinuar: MaterialButton
    private lateinit var btnCancelarAbajo: MaterialButton
    private lateinit var btnAtrasArriba: LinearLayout
    private lateinit var tvIndicadorPaso: TextView

    // Círculos y líneas del stepper
    private lateinit var circulo1: TextView
    private lateinit var circulo2: TextView
    private lateinit var circulo3: TextView
    private lateinit var circulo4: TextView
    private lateinit var linea1_2: View
    private lateinit var linea2_3: View
    private lateinit var linea3_4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_create)

        // se vincula las vistas
        inicializarVistas()

        btnContinuar.setOnClickListener {
            if (pasoActual < 4) {
                pasoActual++
                actualizarInterfaz()
            } else {
                // estas variables se utilizaran cuando se guarden en base de datos
                Toast.makeText(this, "¡Cita guardada exitosamente!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val accionAtras = View.OnClickListener {
            if (pasoActual > 1) {
                pasoActual--
                actualizarInterfaz()
            } else {
                finish()
            }
        }

        btnCancelarAbajo.setOnClickListener(accionAtras)
        btnAtrasArriba.setOnClickListener(accionAtras)

        // se inicializa la interfaz si cuando este en el paso 1
        actualizarInterfaz()
    }

    private fun inicializarVistas() {
        contenedorPasos = findViewById(R.id.step_container)
        btnContinuar = findViewById(R.id.btn_continue)
        btnCancelarAbajo = findViewById(R.id.btn_cancel_bottom)
        btnAtrasArriba = findViewById(R.id.btn_back)
        tvIndicadorPaso = findViewById(R.id.tv_step_indicator)

        circulo1 = findViewById(R.id.step1_circle)
        circulo2 = findViewById(R.id.step2_circle)
        circulo3 = findViewById(R.id.step3_circle)
        circulo4 = findViewById(R.id.step4_circle)

        linea1_2 = findViewById(R.id.line_1_2)
        linea2_3 = findViewById(R.id.line_2_3)
        linea3_4 = findViewById(R.id.line_3_4)
    }

    private fun actualizarInterfaz() {
        // se actualizan los botones y los pasos - el pasoActual se lee de la variable global
        tvIndicadorPaso.text = "Paso $pasoActual de 4"

        // se modifica el texto en base al paso que este
        if (pasoActual == 4) {
            btnContinuar.text = "Confirmar cita"
            btnCancelarAbajo.text = "Volver al inicio"
        } else {
            btnContinuar.text = "Continuar"
            btnCancelarAbajo.text = "Cancelar"
        }

        // se muestra el contenedor en base al paso que este
        contenedorPasos.removeAllViews() // Limpiamos el paso anterior
        val layoutAInyectar = when (pasoActual) {
            1 -> R.layout.layout_step1_pet
            2 -> R.layout.layout_step2_service
            3 -> R.layout.layout_step3_datetime
            4 -> R.layout.layout_step4_confirm
            else -> R.layout.layout_step1_pet
        }
        // Inflamos el nuevo layout y lo metemos en el contenedor
        layoutInflater.inflate(layoutAInyectar, contenedorPasos, true)

        // se pintan los numeros superiores para indicar en que paso se encuentra
        pintarStepper()
    }

    private fun pintarStepper() {

        val colorVerdeActivo = ContextCompat.getDrawable(this, R.drawable.bg_stepper_active)
        val colorGrisInactivo = ContextCompat.getDrawable(this, R.drawable.bg_stepper_inactive)
        val textoBlanco = ContextCompat.getColor(this, R.color.bg_white)
        val textoGris = ContextCompat.getColor(this, R.color.text_gray)
        val lineaVerde = ContextCompat.getColor(this, R.color.green_primary)
        val lineaGris = android.graphics.Color.parseColor("#E5E7EB")


        val circulos = listOf(circulo1, circulo2, circulo3, circulo4)
        circulos.forEach {
            it.background = colorGrisInactivo
            it.setTextColor(textoGris)
        }
        linea1_2.setBackgroundColor(lineaGris)
        linea2_3.setBackgroundColor(lineaGris)
        linea3_4.setBackgroundColor(lineaGris)

        // se va pintando los colores en base al paso que se encuentre
        for (i in 0 until pasoActual) {
            circulos[i].background = colorVerdeActivo
            circulos[i].setTextColor(textoBlanco)
        }

        if (pasoActual >= 2) linea1_2.setBackgroundColor(lineaVerde)
        if (pasoActual >= 3) linea2_3.setBackgroundColor(lineaVerde)
        if (pasoActual >= 4) linea3_4.setBackgroundColor(lineaVerde)

        // el boton continuar se pone en verde si la infromación ya esta ingresada
        btnContinuar.setBackgroundColor(ContextCompat.getColor(this, R.color.green_primary))
    }
}