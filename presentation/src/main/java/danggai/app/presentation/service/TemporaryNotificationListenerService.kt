package danggai.app.presentation.service

import android.service.notification.NotificationListenerService
import danggai.app.presentation.core.util.log

class TemporaryNotificationListenerService: NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        log.e("MyNotificationListener.onListenerConnected()")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        log.e("MyNotificationListener.onListenerDisconnected()")
    }

}