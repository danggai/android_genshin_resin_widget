package danggai.app.presentation.core.util

import android.content.Context
import android.content.SharedPreferences
import danggai.app.presentation.BuildConfig
import danggai.domain.util.Constant
import java.util.*


object PreferenceManager {
    const val PREFERENCES_NAME = "danggai.app.resinwidget"
    private const val DEFAULT_VALUE_STRING = ""
    private const val DEFAULT_VALUE_BOOLEAN = false
    private const val DEFAULT_VALUE_INT = -1
    private const val DEFAULT_VALUE_LONG = -1L
    private const val DEFAULT_VALUE_FLOAT = -1f
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
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
    fun getBooleanFirstLaunch(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_FIRST_LAUNCH, true)
    }
    fun setBooleanFirstLaunch(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_FIRST_LAUNCH, value)
    }


    fun getIntServer(context: Context): Int {
        return getInt(context, Constant.PREF_SERVER)
    }
    fun setIntServer(context: Context, value: Int) {
        setInt(context, Constant.PREF_SERVER, value)
    }

    fun getStringUid(context: Context): String {
        return getString(context, Constant.PREF_UID)
    }
    fun setStringUid(context: Context, value: String) {
        setString(context, Constant.PREF_UID, value)
    }

    fun getStringCookie(context: Context): String {
        return getString(context, Constant.PREF_COOKIE)
    }
    fun setStringCookie(context: Context, value: String) {
        setString(context, Constant.PREF_COOKIE, value)
    }


    fun getIntCurrentResin(context: Context): Int {
        return getInt(context, Constant.PREF_CURRENT_RESIN)
    }
    fun setIntCurrentResin(context: Context, value: Int) {
        setInt(context, Constant.PREF_CURRENT_RESIN, value)
    }

    fun getIntMaxResin(context: Context): Int {
        return getInt(context, Constant.PREF_MAX_RESIN)
    }
    fun setIntMaxResin(context: Context, value: Int) {
        setInt(context, Constant.PREF_MAX_RESIN, value)
    }

    fun getStringResinRecoveryTime(context: Context): String {
        return getString(context, Constant.PREF_RESIN_RECOVERY_TIME)
    }
    fun setStringResinRecoveryTime(context: Context, value: String) {
        setString(context, Constant.PREF_RESIN_RECOVERY_TIME, value)
    }


    fun getIntCurrentDailyCommission(context: Context): Int {
        return getInt(context, Constant.PREF_CURRENT_DAILY_COMMISSION)
    }
    fun setIntCurrentDailyCommission(context: Context, value: Int) {
        setInt(context, Constant.PREF_CURRENT_DAILY_COMMISSION, value)
    }

    fun getIntMaxDailyCommission(context: Context): Int {
        return getInt(context, Constant.PREF_MAX_DAILY_COMMISSION)
    }
    fun setIntMaxDailyCommission(context: Context, value: Int) {
        setInt(context, Constant.PREF_MAX_DAILY_COMMISSION, value)
    }

    fun getBooleanGetDailyCommissionReward(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_GET_DAILY_COMMISSION_REWARD, false)
    }
    fun setBooleanGetDailyCommissionReward(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_GET_DAILY_COMMISSION_REWARD, value)
    }



    fun getIntCurrentWeeklyBoss(context: Context): Int {
        return getInt(context, Constant.PREF_CURRENT_WEEKLY_BOSS)
    }
    fun setIntCurrentWeeklyBoss(context: Context, value: Int) {
        setInt(context, Constant.PREF_CURRENT_WEEKLY_BOSS, value)
    }

    fun getIntMaxWeeklyBoss(context: Context): Int {
        return getInt(context, Constant.PREF_MAX_WEEKLY_BOSS)
    }
    fun setIntMaxWeeklyBoss(context: Context, value: Int) {
        setInt(context, Constant.PREF_MAX_WEEKLY_BOSS, value)
    }


    fun getIntCurrentHomeCoin(context: Context): Int {
        return getInt(context, Constant.PREF_CURRENT_HOME_COIN)
    }
    fun setIntCurrentHomeCoin(context: Context, value: Int) {
        setInt(context, Constant.PREF_CURRENT_HOME_COIN, value)
    }

    fun getIntMaxHomeCoin(context: Context): Int {
        return getInt(context, Constant.PREF_MAX_HOME_COIN)
    }
    fun setIntMaxHomeCoin(context: Context, value: Int) {
        setInt(context, Constant.PREF_MAX_HOME_COIN, value)
    }

    fun getStringHomeCoinRecoveryTime(context: Context): String {
        return getString(context, Constant.PREF_HOME_COIN_RECOVERY_TIME)
    }
    fun setStringHomeCoinRecoveryTime(context: Context, value: String) {
        setString(context, Constant.PREF_HOME_COIN_RECOVERY_TIME, value)
    }


    fun getIntCurrentExpedition(context: Context): Int {
        return getInt(context, Constant.PREF_CURRENT_EXPEDITION)
    }
    fun setIntCurrentExpedition(context: Context, value: Int) {
        setInt(context, Constant.PREF_CURRENT_EXPEDITION, value)
    }

    fun getIntMaxExpedition(context: Context): Int {
        return getInt(context, Constant.PREF_MAX_EXPEDITION)
    }
    fun setIntMaxExpedition(context: Context, value: Int) {
        setInt(context, Constant.PREF_MAX_EXPEDITION, value)
    }

    fun getStringExpeditionTime(context: Context): String {
        return getString(context, Constant.PREF_EXPEDITION_TIME)
    }
    fun setStringExpeditionTime(context: Context, value: String) {
        setString(context, Constant.PREF_EXPEDITION_TIME, value)
    }


    fun getStringRecentSyncTime(context: Context): String {
        return getString(context, Constant.PREF_RECENT_SYNC_TIME)
    }
    fun setStringRecentSyncTime(context: Context, value: String) {
        setString(context, Constant.PREF_RECENT_SYNC_TIME, value)
    }

    fun getLongAutoRefreshPeriod(context: Context): Long {
        return getLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, Constant.PREF_DEFAULT_REFRESH_PERIOD)
    }
    fun setLongAutoRefreshPeriod(context: Context, value: Long) {
        setLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, value)
    }

    fun getIntResinTimeNotation(context: Context): Int {
        return getInt(context, Constant.PREF_RESIN_TIME_NOTATION)
    }
    fun setIntResinTimeNotation(context: Context, value: Int) {
        setInt(context, Constant.PREF_RESIN_TIME_NOTATION, value)
    }

    fun getIntDetailTimeNotation(context: Context): Int {
        return getInt(context, Constant.PREF_DETAIL_TIME_NOTATION)
    }
    fun setIntDetailTimeNotation(context: Context, value: Int) {
        setInt(context, Constant.PREF_DETAIL_TIME_NOTATION, value)
    }

    fun getIntWidgetTheme(context: Context): Int {
        return getInt(context, Constant.PREF_WIDGET_THEME)
    }
    fun setIntWidgetTheme(context: Context, value: Int) {
        setInt(context, Constant.PREF_WIDGET_THEME, value)
    }

    fun getIntWidgetResinImageVisibility(context: Context): Int {
        return getInt(context, Constant.PREF_WIDGET_RESIN_IMAGE_VISIBILITY)
    }
    fun setIntWidgetResinImageVisibility(context: Context, value: Int) {
        setInt(context, Constant.PREF_WIDGET_RESIN_IMAGE_VISIBILITY, value)
    }

    fun getIntWidgetDetailFontSize(context: Context): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_DETAIL, Constant.PREF_WIDGET_DETAIL_FONT_SIZE)
    }
    fun setIntWidgetDetailFontSize(context: Context, value: Int) {
        setInt(context, Constant.PREF_WIDGET_DETAIL_FONT_SIZE, value)
    }

    fun getIntWidgetResinFontSize(context: Context): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_RESIN, Constant.PREF_WIDGET_RESIN_FONT_SIZE)
    }
    fun setIntWidgetResinFontSize(context: Context, value: Int) {
        setInt(context, Constant.PREF_WIDGET_RESIN_FONT_SIZE, value)
    }

    fun getIntBackgroundTransparency(context: Context): Int {
        return getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY)
    }
    fun setIntBackgroundTransparency(context: Context, value: Int) {
        setInt(context, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY, value)
    }

    fun getBooleanWidgetResinDataVisibility(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_RESIN_DATA_VISIBILITY, true)
    }
    fun setBooleanWidgetResinDataVisibility(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_RESIN_DATA_VISIBILITY, value)
    }

    fun getBooleanWidgetDailyCommissionDataVisibility(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_DAILY_COMMISSION_DATA_VISIBILITY, true)
    }
    fun setBooleanWidgetDailyCommissionDataVisibility(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_DAILY_COMMISSION_DATA_VISIBILITY, value)
    }

    fun getBooleanWidgetWeeklyBossDataVisibility(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_WEEKLY_BOSS_DATA_VISIBILITY, true)
    }
    fun setBooleanWidgetWeeklyBossDataVisibility(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_WEEKLY_BOSS_DATA_VISIBILITY, value)
    }

    fun getBooleanWidgetRealmCurrencyDataVisibility(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_REALM_CURRENCY_DATA_VISIBILITY, true)
    }
    fun setBooleanWidgetRealmCurrencyDataVisibility(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_REALM_CURRENCY_DATA_VISIBILITY, value)
    }

    fun getBooleanWidgetExpeditionDataVisibility(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_WIDGET_EXPEDITION_DATA_VISIBILITY, true)
    }
    fun setBooleanWidgetExpeditionDataVisibility(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_WIDGET_EXPEDITION_DATA_VISIBILITY, value)
    }



    fun getBooleanIsValidUserData(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_IS_VALID_USERDATA, false)
    }
    fun setBooleanIsValidUserData(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_IS_VALID_USERDATA, value)
    }

    fun getBooleanNotiEach40Resin(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_EACH_40_RESIN, false)
    }
    fun setBooleanNotiEach40Resin(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_EACH_40_RESIN, value)
    }

    fun getBooleanNoti140Resin(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_140_RESIN, false)
    }
    fun setBooleanNoti140Resin(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_140_RESIN, value)
    }

    fun getBooleanNotiCustomResin(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CUSTOM_RESIN_BOOLEAN, false)
    }
    fun setBooleanNotiCustomResin(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CUSTOM_RESIN_BOOLEAN, value)
    }

    fun getIntCustomTargetResin(context: Context): Int {
        return getInt(context, Constant.PREF_NOTI_CUSTOM_TARGET_RESIN )
    }
    fun setIntCustomTargetResin(context: Context, value: Int) {
        setInt(context, Constant.PREF_NOTI_CUSTOM_TARGET_RESIN, value)
    }

    fun getBooleanNotiExpeditionDone(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_EXPEDITION_DONE, false)
    }
    fun setBooleanNotiExpeditionDone(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_EXPEDITION_DONE, value)
    }

    fun getBooleanNotiHomeCoinFull(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_HOME_COIN_FULL, false)
    }
    fun setBooleanNotiHomeCoinFull(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_HOME_COIN_FULL, value)
    }

    fun getBooleanEnableGenshinAutoCheckIn(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, false)
    }
    fun setBooleanEnableAutoCheckIn(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, value)
    }

    fun getBooleanEnableHonkai3rdAutoCheckIn(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, false)
    }
    fun setBooleanEnableHonkai3rdAutoCheckIn(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, value)
    }

    fun getBooleanNotiCheckInSuccess(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CHECK_IN_SUCCESS, true)
    }
    fun setBooleanNotiCheckInSuccess(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CHECK_IN_SUCCESS, value)
    }

    fun getBooleanNotiCheckInFailed(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_NOTI_CHECK_IN_FAILED, false)
    }
    fun setBooleanNotiCheckInFailed(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_NOTI_CHECK_IN_FAILED, value)
    }

    fun getStringLocale(context: Context): String {
        return getString(context, Constant.PREF_LOCALE, Locale.getDefault().language)
    }
    fun setStringLocale(context: Context, value: String) {
        setString(context, Constant.PREF_LOCALE, value)
    }



    fun getBooleanCheckedUpdateNote(context: Context): Boolean {
        return getBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, false)
    }
    fun setBooleanCheckedUpdateNote(context: Context, value: Boolean) {
        setBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, value)
    }

}