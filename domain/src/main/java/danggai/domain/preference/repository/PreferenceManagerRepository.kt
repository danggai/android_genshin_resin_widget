package danggai.domain.preference.repository

import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDailyNoteData


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


    fun getDailyNoteSettings(): DailyNoteSettings
    fun setDailyNoteSettings(value: DailyNoteSettings)

    fun getCheckInSettings(): CheckInSettings
    fun setCheckInSettings(value: CheckInSettings)

    fun getResinWidgetDesignSettings(): ResinWidgetDesignSettings
    fun setResinWidgetDesignSettings(value: ResinWidgetDesignSettings)

    fun getDetailWidgetDesignSettings(): DetailWidgetDesignSettings
    fun setDetailWidgetDesignSettings(value: DetailWidgetDesignSettings)

    fun getSelectedCharacterIdList(): List<Int>
    fun setSelectedCharacterIdList(value: List<Int>)


    fun getGenshinDailyNote(uid: String): GenshinDailyNoteData
    fun setGenshinDailyNote(uid: String, value: GenshinDailyNoteData)

    fun getHonkaiSrDailyNote(uid: String): HonkaiSrDailyNoteData
    fun setHonkaiSrDailyNote(uid: String, value: HonkaiSrDailyNoteData)

    fun getStringRecentSyncTime(uid: String): String
    fun setStringRecentSyncTime(uid: String, value: String)

    fun getStringExpeditionTime(uid: String): String
    fun setStringExpeditionTime(uid: String, value: String)

    fun getStringHonkaiSrExpeditionTime(uid: String): String
    fun setStringHonkaiSrExpeditionTime(uid: String, value: String)

    fun setBooleanEnableAutoCheckIn(value: Boolean)
    fun setBooleanEnableHonkai3rdAutoCheckIn(value: Boolean)


    fun getStringRecentDailyCommissionNotiDate(uid: String): String
    fun setStringRecentDailyCommissionNotiDate(uid: String, value: String)

    fun getStringRecentWeeklyBossNotiDate(uid: String): String
    fun setStringRecentWeeklyBossNotiDate(uid: String, value: String)


    fun getStringLocale(): String
    fun setStringLocale(value: String)

    fun getBooleanCheckedUpdateNote(): Boolean
    fun setBooleanCheckedUpdateNote(value: Boolean)
}