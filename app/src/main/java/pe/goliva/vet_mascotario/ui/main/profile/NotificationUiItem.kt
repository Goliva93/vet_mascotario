package pe.goliva.vet_mascotario.ui.main.profile

data class NotificationUiItem(
    val title: String,
    val message: String,
    val timeLabel: String,
    val iconRes: Int,
    val iconTintRes: Int,
    val iconBgRes: Int,
    val isUnread: Boolean,
    val isHighlighted: Boolean
)
