package danggai.data.preference.repository

import android.content.Context
import android.content.SharedPreferences
import danggai.data.BuildConfig
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import java.util.*
import javax.inject.Inject

class PreferenceManagerRepositoryImpl @Inject constructor(
    private val context: Context
): PreferenceManagerRepository {
    companion object {
        private const val PREFERENCES_NAME = "danggai.app.resinwidget"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f
        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setString(context: Context, key: String?, value: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setInt(context: Context, key: String?, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setLong(context: Context, key: String?, value: Long) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setFloat(context: Context, key: String?, value: Float) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getString(context: Context, key: String?): String {
        val prefs = getPreferences(context)
        return prefs.getString(key, DEFAULT_VALUE_STRING)?: DEFAULT_VALUE_STRING
    }
    fun getString(context: Context, key: String?, default: String): String {
        val prefs = getPreferences(context)
        return prefs.getString(key, default)?:default
    }

    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getBoolean(context: Context, key: String?, default: Boolean): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, default)
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getInt(context: Context, key: String?): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }
    fun getIntDefault(context: Context, default: Int, key: String?): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, default)
    }

    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getLong(context: Context, key: String?): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    fun getLong(context: Context, key: String?, default: Long): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, default)
    }

    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getFloat(context: Context, key: String?): Float {
        val prefs = getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    fun removeKey(context: Context, key: String?) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.apply()
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.clear()
        edit.apply()
    }
    
    /**
     * 커스텀 함수
     */
    override fun getBooleanFirstLaunch(): Boolean {
        return getBoolean(context, Constant.PREF_FIRST_LAUNCH, true)
    }
    override fun setBooleanFirstLaunch(value: Boolean) {
        setBoolean(context, Constant.PREF_FIRST_LAUNCH, value)
    }


    override fun getIntServer(): Int {
        return getInt(context, Constant.PREF_SERVER)
    }
    override fun setIntServer(value: Int) {
        setInt(context, Constant.PREF_SERVER, value)
    }

    override fun getStringUid(): String {
        return getString(context, Constant.PREF_UID)
    }
    override fun setStringUid(value: String) {
        setString(context, Constant.PREF_UID, value)
    }

    override fun getStringCookie(): String {
        return getString(context, Constant.PREF_COOKIE)
    }
    override fun setStringCookie(value: String) {
        setString(context, Constant.PREF_COOKIE, value)
    }


    override fun getIntCurrentResin(): Int {
        return getInt(context, Constant.PREF_CURRENT_RESIN)
    }
    override fun setIntCurrentResin(value: Int) {
        setInt(context, Constant.PREF_CURRENT_RESIN, value)
    }

    override fun getIntMaxResin(): Int {
        return getInt(context, Constant.PREF_MAX_RESIN)
    }
    override fun setIntMaxResin(value: Int) {
        setInt(context, Constant.PREF_MAX_RESIN, value)
    }

    override fun getStringResinRecoveryTime(): String {
        return getString(context, Constant.PREF_RESIN_RECOVERY_TIME)
    }
    override fun setStringResinRecoveryTime(value: String) {
        setString(context, Constant.PREF_RESIN_RECOVERY_TIME, value)
    }


    override fun getIntCurrentDailyCommission(): Int {
        return getInt(context, Constant.PREF_CURRENT_DAILY_COMMISSION)
    }
    override fun setIntCurrentDailyCommission(value: Int) {
        setInt(context, Constant.PREF_CURRENT_DAILY_COMMISSION, value)
    }

    override fun getIntMaxDailyCommission(): Int {
        return getInt(context, Constant.PREF_MAX_DAILY_COMMISSION)
    }
    override fun setIntMaxDailyCommission(value: Int) {
        setInt(context, Constant.PREF_MAX_DAILY_COMMISSION, value)
    }

    override fun getBooleanGetDailyCommissionReward(): Boolean {
        return getBoolean(context, Constant.PREF_GET_DAILY_COMMISSION_REWARD, false)
    }
    override fun setBooleanGetDailyCommissionReward(value: Boolean) {
        setBoolean(context, Constant.PREF_GET_DAILY_COMMISSION_REWARD, value)
    }



    override fun getIntCurrentWeeklyBoss(): Int {
        return getInt(context, Constant.PREF_CURRENT_WEEKLY_BOSS)
    }
    override fun setIntCurrentWeeklyBoss(value: Int) {
        setInt(context, Constant.PREF_CURRENT_WEEKLY_BOSS, value)
    }

    override fun getIntMaxWeeklyBoss(): Int {
        return getInt(context, Constant.PREF_MAX_WEEKLY_BOSS)
    }
    override fun setIntMaxWeeklyBoss(value: Int) {
        setInt(context, Constant.PREF_MAX_WEEKLY_BOSS, value)
    }


    override fun getIntCurrentHomeCoin(): Int {
        return getInt(context, Constant.PREF_CURRENT_HOME_COIN)
    }
    override fun setIntCurrentHomeCoin(value: Int) {
        setInt(context, Constant.PREF_CURRENT_HOME_COIN, value)
    }

    override fun getIntMaxHomeCoin(): Int {
        return getInt(context, Constant.PREF_MAX_HOME_COIN)
    }
    override fun setIntMaxHomeCoin(value: Int) {
        setInt(context, Constant.PREF_MAX_HOME_COIN, value)
    }

    override fun getStringHomeCoinRecoveryTime(): String {
        return getString(context, Constant.PREF_HOME_COIN_RECOVERY_TIME)
    }
    override fun setStringHomeCoinRecoveryTime(value: String) {
        setString(context, Constant.PREF_HOME_COIN_RECOVERY_TIME, value)
    }


    override fun getIntCurrentExpedition(): Int {
        return getInt(context, Constant.PREF_CURRENT_EXPEDITION)
    }

    override fun setIntCurrentExpedition(value: Int) {
        setInt(context, Constant.PREF_CURRENT_EXPEDITION, value)
    }

    override fun getIntMaxExpedition(): Int {
        return getInt(context, Constant.PREF_MAX_EXPEDITION)
    }
    override fun setIntMaxExpedition(value: Int) {
        setInt(context, Constant.PREF_MAX_EXPEDITION, value)
    }

    override fun getStringExpeditionTime(): String {
        return getString(context, Constant.PREF_EXPEDITION_TIME)
    }
    override fun setStringExpeditionTime(value: String) {
        setString(context, Constant.PREF_EXPEDITION_TIME, value)
    }


    override fun getStringRecentSyncTime(): String {
        return getString(context, Constant.PREF_RECENT_SYNC_TIME)
    }
    override fun setStringRecentSyncTime(value: String) {
        setString(context, Constant.PREF_RECENT_SYNC_TIME, value)
    }

    override fun getLongAutoRefreshPeriod(): Long {
        return getLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, Constant.PREF_DEFAULT_REFRESH_PERIOD)
    }
    override fun setLongAutoRefreshPeriod(value: Long) {
        setLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, value)
    }

    override fun getIntResinTimeNotation(): Int {
        return getInt(context, Constant.PREF_RESIN_TIME_NOTATION)
    }
    override fun setIntResinTimeNotation(value: Int) {
        setInt(context, Constant.PREF_RESIN_TIME_NOTATION, value)
    }

    override fun getIntDetailTimeNotation(): Int {
        return getInt(context, Constant.PREF_DETAIL_TIME_NOTATION)
    }
    override fun setIntDetailTimeNotation(value: Int) {
        setInt(context, Constant.PREF_DETAIL_TIME_NOTATION, value)
    }

    override fun getIntWidgetTheme(): Int {
        return getInt(context, Constant.PREF_WIDGET_THEME)
    }
    override fun setIntWidgetTheme(value: Int) {
        setInt(context, Constant.PREF_WIDGET_THEME, value)
    }

    override fun getIntWidgetResinImageVisibility(): Int {
        return getInt(context, Constant.PREF_WIDGET_RESIN_IMAGE_VISIBILITY)
    }
    override fun setIntWidgetResinImageVisibility(value: Int) {
        setInt(context, Constant.PREF_WIDGET_RESIN_IMAGE_VISIBILITY, value)
    }

    override fun getIntWidgetDetailFontSize(): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_DETAIL, Constant.PREF_WIDGET_DETAIL_FONT_SIZE)
    }
    override fun setIntWidgetDetailFontSize(value: Int) {
        setInt(context, Constant.PREF_WIDGET_DETAIL_FONT_SIZE, value)
    }

    override fun getIntWidgetResinFontSize(): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_RESIN, Constant.PREF_WIDGET_RESIN_FONT_SIZE)
    }
    override fun setIntWidgetResinFontSize(value: Int) {
        setInt(context, Constant.PREF_WIDGET_RESIN_FONT_SIZE, value)
    }

    override fun getIntBackgroundTransparency(): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY)
    }
    override fun setIntBackgroundTransparency(value: Int) {
        setInt(context, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY, value)
    }

    override fun getBooleanWidgetResinDataVisibility(): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_RESIN_DATA_VISIBILITY, true)
    }
    override fun setBooleanWidgetResinDataVisibility(value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_RESIN_DATA_VISIBILITY, value)
    }

    override fun getBooleanWidgetDailyCommissionDataVisibility(): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_DAILY_COMMISSION_DATA_VISIBILITY, true)
    }
    override fun setBooleanWidgetDailyCommissionDataVisibility(value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_DAILY_COMMISSION_DATA_VISIBILITY, value)
    }

    override fun getBooleanWidgetWeeklyBossDataVisibility(): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_WEEKLY_BOSS_DATA_VISIBILITY, true)
    }
    override fun setBooleanWidgetWeeklyBossDataVisibility(value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_WEEKLY_BOSS_DATA_VISIBILITY, value)
    }

    override fun getBooleanWidgetRealmCurrencyDataVisibility(): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_REALM_CURRENCY_DATA_VISIBILITY, true)
    }
    override fun setBooleanWidgetRealmCurrencyDataVisibility(value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_REALM_CURRENCY_DATA_VISIBILITY, value)
    }

    override fun getBooleanWidgetExpeditionDataVisibility(): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_EXPEDITION_DATA_VISIBILITY, true)
    }
    override fun setBooleanWidgetExpeditionDataVisibility(value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_EXPEDITION_DATA_VISIBILITY, value)
    }



    override fun getBooleanIsValidUserData(): Boolean {
        return getBoolean(context, Constant.PREF_IS_VALID_USERDATA, false)
    }
    override fun setBooleanIsValidUserData(value: Boolean) {
        setBoolean(context, Constant.PREF_IS_VALID_USERDATA, value)
    }

    override fun getBooleanNotiEach40Resin(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_EACH_40_RESIN, false)
    }
    override fun setBooleanNotiEach40Resin(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_EACH_40_RESIN, value)
    }

    override fun getBooleanNoti140Resin(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_140_RESIN, false)
    }
    override fun setBooleanNoti140Resin(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_140_RESIN, value)
    }

    override fun getBooleanNotiCustomResin(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CUSTOM_RESIN_BOOLEAN, false)
    }
    override fun setBooleanNotiCustomResin(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CUSTOM_RESIN_BOOLEAN, value)
    }

    override fun getIntCustomTargetResin(): Int {
        return getInt(context, Constant.PREF_NOTI_CUSTOM_TARGET_RESIN )
    }
    override fun setIntCustomTargetResin(value: Int) {
        setInt(context, Constant.PREF_NOTI_CUSTOM_TARGET_RESIN, value)
    }

    override fun getBooleanNotiExpeditionDone(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_EXPEDITION_DONE, false)
    }
    override fun setBooleanNotiExpeditionDone(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_EXPEDITION_DONE, value)
    }

    override fun getBooleanNotiHomeCoinFull(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_HOME_COIN_FULL, false)
    }
    override fun setBooleanNotiHomeCoinFull(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_HOME_COIN_FULL, value)
    }

    override fun getBooleanEnableGenshinAutoCheckIn(): Boolean {
        return getBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, false)
    }
    override fun setBooleanEnableAutoCheckIn(value: Boolean) {
        setBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, value)
    }

    override fun getBooleanEnableHonkai3rdAutoCheckIn(): Boolean {
        return getBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, false)
    }
    override fun setBooleanEnableHonkai3rdAutoCheckIn(value: Boolean) {
        setBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, value)
    }

    override fun getBooleanNotiCheckInSuccess(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CHECK_IN_SUCCESS, true)
    }
    override fun setBooleanNotiCheckInSuccess(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CHECK_IN_SUCCESS, value)
    }

    override fun getBooleanNotiCheckInFailed(): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CHECK_IN_FAILED, false)
    }
    override fun setBooleanNotiCheckInFailed(value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CHECK_IN_FAILED, value)
    }

    override fun getStringLocale(): String {
        return getString(context, Constant.PREF_LOCALE, Locale.getDefault().language)
    }
    override fun setStringLocale(value: String) {
        setString(context, Constant.PREF_LOCALE, value)
    }
    
    override fun getBooleanCheckedUpdateNote(): Boolean {
        return getBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, false)
    }
    override fun setBooleanCheckedUpdateNote(value: Boolean) {
        setBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, value)
    }
}