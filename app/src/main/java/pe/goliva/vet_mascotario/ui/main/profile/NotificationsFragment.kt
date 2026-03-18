package pe.goliva.vet_mascotario.ui.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNotificationsBinding.bind(view)

        setBottomNavVisible(false)
        setupClicks()
        setupNotifications()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupNotifications() {
        val items = listOf(
            NotificationUiItem(
                title = "Cita confirmada",
                message = "Tu cita para Max el 25 Feb a las 10:00 AM ha sido confirmada",
                timeLabel = "Hace 2 horas",
                iconRes = R.drawable.ic_check_circle_outline,
                iconTintRes = R.color.green_primary,
                iconBgRes =  R.drawable.bg_profile_icon_green,
                isUnread = true,
                isHighlighted = true
            ),
            NotificationUiItem(
                title = "Recordatorio de cita",
                message = "Tienes una cita mañana con Luna a las 2:00 PM",
                timeLabel = "Hace 1 día",
                iconRes = R.drawable.ic_calendar_outline,
                iconTintRes = R.color.yellow_accent,
                iconBgRes = R.drawable.bg_profile_icon_yellow,
                isUnread = true,
                isHighlighted = true
            ),
            NotificationUiItem(
                title = "Cita cancelada",
                message = "Tu cita del 20 Feb ha sido cancelada",
                timeLabel = "Hace 3 días",
                iconRes = R.drawable.ic_close_outline,
                iconTintRes = R.color.status_error,
                iconBgRes = R.drawable.bg_profile_icon_gray, //R.color.red_light_bg,
                isUnread = false,
                isHighlighted = false
            ),
            NotificationUiItem(
                title = "Pago pendiente",
                message = "Recuerda subir tu evidencia de pago para la cita de Max",
                timeLabel = "Hace 5 días",
                iconRes = R.drawable.ic_warning_outline,
                iconTintRes = R.color.yellow_accent,
                iconBgRes = R.drawable.bg_profile_icon_yellow,
                isUnread = false,
                isHighlighted = false
            )
        )

        binding.rvNotifications.adapter = NotificationsAdapter(items)
    }

    private fun setBottomNavVisible(visible: Boolean) {
        requireActivity().findViewById<View>(R.id.bottom_nav)?.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        setBottomNavVisible(true)
        super.onDestroyView()
        _binding = null
    }
}