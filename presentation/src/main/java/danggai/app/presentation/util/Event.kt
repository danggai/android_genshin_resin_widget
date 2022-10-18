package danggai.app.presentation.util

import danggai.domain.db.account.entity.Account

sealed class Event {
    data class MakeToast(val message: String): Event()
    data class FinishThisActivity(val unit: Unit = Unit): Event()

    data class RefreshWebView(val unit: Unit = Unit): Event()

    data class WidgetRefreshNotWork(val unit: Unit = Unit): Event()
    data class GetCookie(val unit: Unit = Unit): Event()
    data class WhenDailyNoteIsPrivate(val unit: Unit = Unit): Event()
    data class StartWidgetDesignActivity(val unit: Unit = Unit): Event()
    data class StartNewHoyolabAccountActivity(val unit: Unit = Unit): Event()
    data class StartManageAccount(val account: Account): Event()

    data class ChangeLanguage(val unit: Unit = Unit): Event()

    data class StartShutRefreshWorker(val isValid: Boolean): Event()
    data class StartShutCheckInWorker(val isValid: Boolean): Event()
}