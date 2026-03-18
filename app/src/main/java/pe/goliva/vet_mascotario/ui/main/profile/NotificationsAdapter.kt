package pe.goliva.vet_mascotario.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.databinding.ItemNotificationBinding

class NotificationsAdapter(
    private val items: List<NotificationUiItem>
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationUiItem) {
            binding.tvNotificationTitle.text = item.title
            binding.tvNotificationMessage.text = item.message
            binding.tvNotificationTime.text = item.timeLabel

            binding.ivNotificationIcon.setImageResource(item.iconRes)
            binding.ivNotificationIcon.setColorFilter(
                ContextCompat.getColor(binding.root.context, item.iconTintRes)
            )
            binding.layoutNotificationIcon.setBackgroundResource(item.iconBgRes)

            binding.viewUnreadDot.visibility =
                if (item.isUnread) android.view.View.VISIBLE else android.view.View.GONE

            binding.cardNotification.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (item.isHighlighted) R.color.green_primary else R.color.profile_card_stroke
            )
            binding.cardNotification.strokeWidth =
                if (item.isHighlighted) 2 else 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}