package danggai.domain.preference.repository


interface PreferenceManagerRepository {
    fun getBooleanFirstLaunch(): Boolean
    fun setBooleanFirstLaunch(value: Boolean)
    fun getBooleanIsValidUserData(): Boolean
    fun setBooleanIsValidUserData(value: Boolean)

    fun getIntServer(): Int
    fun setIntServer(value: Int)
    fun getStringUid(): String
    fun setStringUid(value: String)
    fun getStringCookie(): String
    fun setStringCookie(value: String)

    fun getIntCurrentResin(): Int
    fun setIntCurrentResin(value: Int)
    fun getIntMaxResin(): Int
    fun setIntMaxResin(value: Int)
    fun getStringResinRecoveryTime(): String
    fun setStringResinRecoveryTime(value: String)
    fun getIntCurrentDailyCommission(): Int
    fun setIntCurrentDailyCommission(value: Int)
    fun getIntMaxDailyCommission(): Int
    fun setIntMaxDailyCommission(value: Int)
    fun getBooleanGetDailyCommissionReward(): Boolean
    fun setBooleanGetDailyCommissionReward(value: Boolean)
    fun getIntCurrentWeeklyBoss(): Int
    fun setIntCurrentWeeklyBoss(value: Int)
    fun getIntMaxWeeklyBoss(): Int
    fun setIntMaxWeeklyBoss(value: Int)
    fun getIntCurrentHomeCoin(): Int
    fun setIntCurrentHomeCoin(value: Int)
    fun getIntMaxHomeCoin(): Int
    fun setIntMaxHomeCoin(value: Int)
    fun getStringHomeCoinRecoveryTime(): String
    fun setStringHomeCoinRecoveryTime(value: String)
    fun getIntCurrentExpedition(): Int
    fun setIntCurrentExpedition(value: Int)
    fun getIntMaxExpedition(): Int
    fun setIntMaxExpedition(value: Int)
    fun getStringExpeditionTime(): String
    fun setStringExpeditionTime(value: String)
    fun getStringRecentSyncTime(): String
    fun setStringRecentSyncTime(value: String)

    fun getLongAutoRefreshPeriod(): Long
    fun setLongAutoRefreshPeriod(value: Long)
    fun getIntResinTimeNotation(): Int
    fun setIntResinTimeNotation(value: Int)
    fun getIntDetailTimeNotation(): Int
    fun setIntDetailTimeNotation(value: Int)
    fun getIntWidgetTheme(): Int
    fun setIntWidgetTheme(value: Int)
    fun getIntWidgetResinImageVisibility(): Int
    fun setIntWidgetResinImageVisibility(value: Int)
    fun getIntWidgetDetailFontSize(): Int
    fun setIntWidgetDetailFontSize(value: Int)
    fun getIntWidgetResinFontSize(): Int
    fun setIntWidgetResinFontSize(value: Int)
    fun getIntBackgroundTransparency(): Int
    fun setIntBackgroundTransparency(value: Int)
    fun getBooleanWidgetResinDataVisibility(): Boolean
    fun setBooleanWidgetResinDataVisibility(value: Boolean)
    fun getBooleanWidgetDailyCommissionDataVisibility(): Boolean
    fun setBooleanWidgetDailyCommissionDataVisibility(value: Boolean)
    fun getBooleanWidgetWeeklyBossDataVisibility(): Boolean
    fun setBooleanWidgetWeeklyBossDataVisibility(value: Boolean)
    fun getBooleanWidgetRealmCurrencyDataVisibility(): Boolean
    fun setBooleanWidgetRealmCurrencyDataVisibility(value: Boolean)
    fun getBooleanWidgetExpeditionDataVisibility(): Boolean
    fun setBooleanWidgetExpeditionDataVisibility(value: Boolean)

    fun getBooleanNotiEach40Resin(): Boolean
    fun setBooleanNotiEach40Resin(value: Boolean)
    fun getBooleanNoti140Resin(): Boolean
    fun setBooleanNoti140Resin(value: Boolean)
    fun getBooleanNotiCustomResin(): Boolean
    fun setBooleanNotiCustomResin(value: Boolean)
    fun getIntCustomTargetResin(): Int
    fun setIntCustomTargetResin(value: Int)
    fun getBooleanNotiExpeditionDone(): Boolean
    fun setBooleanNotiExpeditionDone(value: Boolean)
    fun getBooleanNotiHomeCoinFull(): Boolean
    fun setBooleanNotiHomeCoinFull(value: Boolean)
    fun getBooleanEnableGenshinAutoCheckIn(): Boolean
    fun setBooleanEnableAutoCheckIn(value: Boolean)
    fun getBooleanEnableHonkai3rdAutoCheckIn(): Boolean
    fun setBooleanEnableHonkai3rdAutoCheckIn(value: Boolean)
    fun getBooleanNotiCheckInSuccess(): Boolean
    fun setBooleanNotiCheckInSuccess(value: Boolean)
    fun getBooleanNotiCheckInFailed(): Boolean
    fun setBooleanNotiCheckInFailed(value: Boolean)

    fun getStringLocale(): String
    fun setStringLocale(value: String)

    fun getBooleanCheckedUpdateNote(): Boolean
    fun setBooleanCheckedUpdateNote(value: Boolean)
}