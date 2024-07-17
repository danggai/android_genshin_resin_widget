package danggai.app.presentation.util

import android.content.Context
import danggai.app.presentation.R
import danggai.domain.db.account.entity.Account
import danggai.domain.local.NotiType
import danggai.domain.util.Constant

object NotificationMapper {
    /**
     * notiType에 알맞는 Account 클래스 내 닉네임을 매핑해서 리턴하는 함수
     */
    fun getNickname(notiType: NotiType, account: Account): String {
        return when (notiType) {
            is NotiType.Genshin -> account.nickname
            is NotiType.StarRail -> account.honkai_sr_nickname
            is NotiType.ZZZ -> account.zzz_nickname

            is NotiType.CheckIn._Genshin,
            is NotiType.CheckIn._Honkai3rd,
            is NotiType.CheckIn.NotFound -> account.nickname

            is NotiType.CheckIn._StarRail -> account.honkai_sr_nickname
            is NotiType.CheckIn._ZZZ -> account.zzz_nickname
        }
    }

    /**
     * notiType에 알맞는 icon의 resourceId를 매핑해서 리턴하는 함수
     */
    fun getNotiIcon(notiType: NotiType): Int {
        return when (notiType) {
            is NotiType.Genshin,
            is NotiType.CheckIn._Genshin,
            NotiType.CheckIn.NotFound.AccountGenshin -> R.drawable.resin

            is NotiType.CheckIn._Honkai3rd,
            NotiType.CheckIn.NotFound.AccountHonkai3rd -> R.drawable.resin

            is NotiType.StarRail,
            is NotiType.CheckIn._StarRail,
            NotiType.CheckIn.NotFound.AccountStarRail -> R.drawable.trailblaze_power

            is NotiType.ZZZ,
            is NotiType.CheckIn._ZZZ,
            NotiType.CheckIn.NotFound.AccountZZZ -> R.drawable.battery
        }
    }

    /**
     * notiType에 알맞는 알림 제목을 매핑해서 리턴하는 함수
     */
    fun getNotiTitle(context: Context, notiType: NotiType): String {
        return when (notiType) {
            NotiType.Genshin.StaminaEach40,
            NotiType.Genshin.Stamina180,
            NotiType.Genshin.StaminaCustom -> context.getString(R.string.push_resin_noti_title)

            NotiType.Genshin.ExpeditionDone -> context.getString(R.string.push_expedition_title)
            NotiType.Genshin.RealmCurrencyFull -> context.getString(R.string.push_realm_currency_title)
            NotiType.Genshin.ParametricTransformerReached -> context.getString(R.string.push_param_trans_title)
            NotiType.Genshin.DailyCommissionNotDone -> context.getString(R.string.push_daily_commission_title)
            NotiType.Genshin.WeeklyBossNotDone -> context.getString(R.string.push_weekly_boss_title)

            NotiType.StarRail.StaminaEach40,
            NotiType.StarRail.Stamina230,
            NotiType.StarRail.StaminaCustom,
            -> context.getString(R.string.push_trail_power_noti_title)

            NotiType.StarRail.ExpeditionDone -> context.getString(R.string.push_assignment_title)

            NotiType.ZZZ.StaminaEach40,
            NotiType.ZZZ.StaminaEach60,
            NotiType.ZZZ.Stamina230,
            NotiType.ZZZ.StaminaCustom,
            -> context.getString(R.string.push_battery_noti_title)

            is NotiType.CheckIn._Genshin,
            NotiType.CheckIn.NotFound.AccountGenshin -> context.getString(R.string.push_genshin_checkin_title)

            is NotiType.CheckIn._Honkai3rd,
            NotiType.CheckIn.NotFound.AccountHonkai3rd -> context.getString(R.string.push_honkai_3rd_checkin_title)

            is NotiType.CheckIn._StarRail,
            NotiType.CheckIn.NotFound.AccountStarRail -> context.getString(R.string.push_honkai_sr_checkin_title)

            is NotiType.CheckIn._ZZZ,
            NotiType.CheckIn.NotFound.AccountZZZ -> context.getString(R.string.push_zzz_checkin_title)
        }
    }

    /**
     * notiType에 알맞는 알림 메세지를 매핑해서 리턴하는 함수
     */
    fun getNotiMsg(context: Context, notiType: NotiType, nickname: String, target: Int?): String {
        return when (notiType) {
            NotiType.Genshin.StaminaEach40 ->
                when (target) {
                    Constant.MAX_RESIN + 40 -> String.format(
                        context.getString(R.string.push_msg_resin_noti_over_240),
                        nickname,
                        target
                    )

                    Constant.MAX_RESIN -> String.format(
                        context.getString(R.string.push_msg_resin_noti_over_200),
                        nickname,
                        target
                    )

                    Constant.MAX_RESIN - 40 -> String.format(
                        context.getString(R.string.push_msg_resin_noti_over_160),
                        nickname,
                        target
                    )

                    else -> String.format(
                        context.getString(R.string.push_msg_resin_noti_over_40),
                        nickname,
                        target
                    )
                }

            NotiType.Genshin.Stamina180 -> String.format(
                context.getString(R.string.push_msg_resin_noti_over_180),
                nickname,
                target
            )

            NotiType.Genshin.StaminaCustom -> String.format(
                context.getString(R.string.push_msg_resin_noti_custom),
                nickname,
                target
            )

            NotiType.Genshin.ExpeditionDone -> String.format(
                context.getString(R.string.push_msg_expedition_done),
                nickname
            )

            NotiType.Genshin.RealmCurrencyFull -> String.format(
                context.getString(R.string.push_msg_realm_currency_full),
                nickname
            )

            NotiType.Genshin.ParametricTransformerReached -> String.format(
                context.getString(R.string.push_msg_param_trans_full),
                nickname
            )

            NotiType.Genshin.DailyCommissionNotDone -> String.format(
                context.getString(R.string.push_msg_daily_commission_yet),
                nickname
            )

            NotiType.Genshin.WeeklyBossNotDone -> String.format(
                context.getString(R.string.push_msg_weekly_boss_yet),
                nickname
            )

            NotiType.StarRail.StaminaEach40 ->
                when (target) {
                    Constant.MAX_TRAILBLAZE_POWER -> String.format(
                        context.getString(R.string.push_msg_trail_power_noti_over_240),
                        nickname,
                        target
                    )

                    else -> String.format(
                        context.getString(R.string.push_msg_trail_power_noti_over_40),
                        nickname,
                        target
                    )
                }

            NotiType.StarRail.Stamina230 -> String.format(
                context.getString(R.string.push_msg_trail_power_noti_over_230),
                nickname,
                target
            )

            NotiType.StarRail.StaminaCustom -> String.format(
                context.getString(R.string.push_msg_trail_power_noti_custom),
                nickname,
                target
            )

            NotiType.StarRail.ExpeditionDone -> String.format(
                context.getString(R.string.push_msg_assignment_done),
                nickname
            )

            NotiType.ZZZ.StaminaEach40,
            NotiType.ZZZ.StaminaEach60 ->
                when (target) {
                    Constant.MAX_BATTERY -> String.format(
                        context.getString(R.string.push_msg_battery_noti_over_240),
                        nickname,
                        target
                    )

                    else -> String.format(
                        context.getString(R.string.push_msg_battery_noti_over_40),
                        nickname,
                        target
                    )
                }

            NotiType.ZZZ.Stamina230 -> String.format(
                context.getString(R.string.push_msg_battery_noti_over_230),
                nickname,
                target
            )

            NotiType.ZZZ.StaminaCustom -> String.format(
                context.getString(R.string.push_msg_battery_noti_custom),
                nickname,
                target
            )

            NotiType.CheckIn._Genshin.Success -> String.format(
                context.getString(R.string.push_msg_checkin_success_genshin),
                nickname
            )

            NotiType.CheckIn._Genshin.Already
            -> String.format(
                context.getString(R.string.push_msg_checkin_already_genshin),
                nickname
            )

            NotiType.CheckIn._Genshin.Failed
            -> String.format(
                context.getString(R.string.push_msg_checkin_failed_genshin),
                nickname
            )

            NotiType.CheckIn._Honkai3rd.Success
            -> String.format(
                context.getString(R.string.push_msg_checkin_success_honkai),
                nickname
            )

            NotiType.CheckIn._Honkai3rd.Already
            -> String.format(
                context.getString(R.string.push_msg_checkin_already_honkai),
                nickname
            )

            NotiType.CheckIn._Honkai3rd.Failed
            -> String.format(
                context.getString(R.string.push_msg_checkin_failed_honkai),
                nickname
            )

            NotiType.CheckIn._StarRail.Success
            -> String.format(
                context.getString(R.string.push_msg_checkin_success_honkai_sr),
                nickname
            )

            NotiType.CheckIn._StarRail.Already
            -> String.format(
                context.getString(R.string.push_msg_checkin_already_honkai_sr),
                nickname
            )

            NotiType.CheckIn._StarRail.Failed
            -> String.format(
                context.getString(R.string.push_msg_checkin_failed_honkai_sr),
                nickname
            )

            NotiType.CheckIn._ZZZ.Success
            -> String.format(
                context.getString(R.string.push_msg_checkin_success_zzz),
                nickname
            )

            NotiType.CheckIn._ZZZ.Already
            -> String.format(
                context.getString(R.string.push_msg_checkin_already_zzz),
                nickname
            )

            NotiType.CheckIn._ZZZ.Failed
            -> String.format(
                context.getString(R.string.push_msg_checkin_failed_zzz),
                nickname
            )

            is NotiType.CheckIn.NotFound -> String.format(
                context.getString(R.string.push_msg_checkin_account_not_found),
                nickname
            )

            NotiType.CheckIn._Genshin.CaptchaOccured
            -> String.format(
                context.getString(R.string.push_msg_checkin_success_genshin_captcha),
                nickname
            )
        }
    }
}