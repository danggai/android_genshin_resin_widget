package danggai.app.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import danggai.domain.db.account.entity.Account
import danggai.domain.local.NotiType
import danggai.domain.util.Constant

object NotificationUtils {

    private val GENSHIN_PACKAGE_NAME_LIST =
        listOf(Constant.PACKAGE_GENSHIN, Constant.PACKAGE_GENSHIN_GALAXY)

    private val HONKAI_SR_PACKAGE_NAME_LIST =
        listOf(Constant.PACKAGE_HONKAI_STARRAIL, Constant.PACKAGE_HONKAI_STARRAIL_GALAXY)

    private val ZZZ_PACKAGE_NAME_LIST = listOf(Constant.PACKAGE_ZENLESS_ZONE_ZERO)

    fun sendNotification(
        notiType: NotiType,
        context: Context,
        account: Account,
        title: String,
        msg: String,
    ) {
        log.e()

        val notificationParams = NotificationMapper.getNotiParams(context, notiType, account)
        val icon = NotificationMapper.getNotiIcon(notiType)

        val notificationManager: NotificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) ?: return

        val builder = NotificationCompat.Builder(context, notificationParams.channelId).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(msg)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            setPriority(notificationParams.priority)

            val pendingIntent = createIntentByNotiType(notiType, context)
            pendingIntent?.let {
                setContentIntent(it)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    notificationParams.channelId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = notificationParams.channelDesc
                }
            )
        }

        notificationManager.notify(notificationParams.notificationId, builder.build())
    }

    private fun createIntentByNotiType(notiType: NotiType, context: Context): PendingIntent? {
        return when (notiType) {
            NotiType.CheckIn._Genshin.CaptchaOccured -> PendingIntent.getActivity(
                context,
                0,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://act.hoyolab.com/ys/event/signin-sea-v3/index.html?act_id=e202102251931481&lang=ko-kr")
                ),
                PendingIntent.FLAG_IMMUTABLE
            )

            is NotiType.Genshin -> createPendingIntent(context, GENSHIN_PACKAGE_NAME_LIST)

            is NotiType.StarRail -> createPendingIntent(context, HONKAI_SR_PACKAGE_NAME_LIST)

            is NotiType.ZZZ -> createPendingIntent(context, ZZZ_PACKAGE_NAME_LIST)

            else -> null
        }
    }

    private fun createPendingIntent(context: Context, packageNames: List<String>): PendingIntent? {
        val installedPackage = packageNames.firstOrNull { isPackageInstalled(context, it) }

        return if (installedPackage != null) {
            // 설치된 패키지가 있으면 해당 앱을 실행
            val launchIntent = context.packageManager.getLaunchIntentForPackage(installedPackage)
            launchIntent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 앱을 새 태스크로 실행
                PendingIntent.getActivity(
                    context,
                    0,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        } else {
            // 설치된 패키지가 없으면 null 반환 (아니면 토스트로 알리거나 다른 동작을 취할 수 있음)
            null
        }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            // 패키지 인포 획득 성공 시 > 설치 O
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            log.e(packageName)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            log.e(packageName)
            false
        }
    }
}