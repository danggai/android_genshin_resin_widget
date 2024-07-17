package danggai.domain.local

data class NotificationParams(
    val notificationId: Int,
    val channelId: String,
    val channelDesc: String,
    val priority: Int,
) {
}
