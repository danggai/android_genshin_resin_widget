package danggai.domain.preference.repository

import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.DailyNoteData


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


    fun getDailyNoteData(): DailyNoteData
    fun setDailyNote(value: DailyNoteData)


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


    fun getStringRecentSyncTime(): String
    fun setStringRecentSyncTime(value: String)

    fun getStringExpeditionTime(): String
    fun setStringExpeditionTime(value: String)

    fun setBooleanEnableAutoCheckIn(value: Boolean)
    fun setBooleanEnableHonkai3rdAutoCheckIn(value: Boolean)

    fun getStringLocale(): String
    fun setStringLocale(value: String)

    fun getBooleanCheckedUpdateNote(): Boolean
    fun setBooleanCheckedUpdateNote(value: Boolean)
}