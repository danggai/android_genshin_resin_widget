package danggai.data.preference.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import danggai.data.BuildConfig
import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
import danggai.domain.network.dailynote.entity.ZZZDailyNoteData
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale
import javax.inject.Inject


class PreferenceManagerRepositoryImpl @Inject constructor(
    private val context: Context,
) : PreferenceManagerRepository {
    companion object {
        private const val PREFERENCES_NAME = "danggai.app.resinwidget"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        fun getPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
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
        return prefs.getString(key, DEFAULT_VALUE_STRING) ?: DEFAULT_VALUE_STRING
    }

    fun getString(context: Context, key: String?, default: String): String {
        val prefs = getPreferences(context)
        return prefs.getString(key, default) ?: default
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
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    fun <T> setT(context: Context, key: String, value: T) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(value)

        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, jsonString).apply()
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> getT(context: Context, key: String): T? {
        //We read JSON String which was saved.
        val prefs = getPreferences(context)
        val value = prefs.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

    private fun setIntArray(context: Context, key: String, values: List<Int>) {
        val prefs: SharedPreferences = getPreferences(context)
        val editor = prefs.edit()

        val jsonArray = JSONArray(values.map { it })

        editor.putString(key, if (values.isNotEmpty()) jsonArray.toString() else null)
        editor.apply()
    }

    private fun getIntArray(context: Context, key: String): List<Int> {
        val prefs: SharedPreferences = getPreferences(context)
        val json = prefs.getString(key, null).orEmpty()

        return try {
            val jsonArray = JSONArray(json)
            List(jsonArray.length()) { jsonArray.optInt(it) }
        } catch (e: JSONException) {
            e.printStackTrace()
            emptyList()
        }
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
    override fun getDailyNoteSettings(): DailyNoteSettings =
        getT<DailyNoteSettings>(context, Constant.PREF_WIDGET_SETTINGS) ?: DailyNoteSettings.EMPTY

    override fun setDailyNoteSettings(value: DailyNoteSettings) =
        setT(context, Constant.PREF_WIDGET_SETTINGS, value)

    override fun getCheckInSettings(): CheckInSettings =
        getT<CheckInSettings>(context, Constant.PREF_CHECK_IN_SETTINGS) ?: CheckInSettings.EMPTY

    override fun setCheckInSettings(value: CheckInSettings) =
        setT(context, Constant.PREF_CHECK_IN_SETTINGS, value)

    override fun getResinWidgetDesignSettings(): ResinWidgetDesignSettings =
        getT<ResinWidgetDesignSettings>(context, Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS)
            ?: ResinWidgetDesignSettings.EMPTY

    override fun setResinWidgetDesignSettings(value: ResinWidgetDesignSettings) =
        setT(context, Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS, value)

    override fun getDetailWidgetDesignSettings(): DetailWidgetDesignSettings =
        getT<DetailWidgetDesignSettings>(context, Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS)
            ?: DetailWidgetDesignSettings.EMPTY

    override fun setDetailWidgetDesignSettings(value: DetailWidgetDesignSettings) =
        setT(context, Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS, value)


    override fun getSelectedCharacterIdList(): List<Int> =
        getIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST)

    override fun setSelectedCharacterIdList(value: List<Int>) =
        setIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST, value)


    override fun getGenshinDailyNote(uid: String): GenshinDailyNoteData =
        getT<GenshinDailyNoteData>(context, Constant.PREF_DAILY_NOTE_DATA + "_$uid")
            ?: GenshinDailyNoteData.EMPTY

    override fun setGenshinDailyNote(uid: String, value: GenshinDailyNoteData) =
        setT(context, Constant.PREF_DAILY_NOTE_DATA + "_$uid", value)

    override fun getHonkaiSrDailyNote(uid: String): HonkaiSrDataLocal =
        getT<HonkaiSrDataLocal>(context, Constant.PREF_HONKAI_SR_DAILY_NOTE_DATA + "_$uid")
            ?: HonkaiSrDataLocal.EMPTY

    override fun setHonkaiSrDailyNote(uid: String, value: HonkaiSrDataLocal) =
        setT(context, Constant.PREF_HONKAI_SR_DAILY_NOTE_DATA + "_$uid", value)

    override fun getZZZDailyNote(uid: String): ZZZDailyNoteData =
        getT<ZZZDailyNoteData>(context, Constant.PREF_ZZZ_DAILY_NOTE_DATA + "_$uid")
            ?: ZZZDailyNoteData.EMPTY

    override fun setZZZDailyNote(uid: String, value: ZZZDailyNoteData) =
        setT(context, Constant.PREF_ZZZ_DAILY_NOTE_DATA + "_$uid", value)

    override fun getStringExpeditionTime(uid: String): String =
        getString(context, Constant.PREF_EXPEDITION_TIME + "_$uid")

    override fun setStringExpeditionTime(uid: String, value: String) =
        setString(context, Constant.PREF_EXPEDITION_TIME + "_$uid", value)

    override fun getStringHonkaiSrExpeditionTime(uid: String): String =
        getString(context, Constant.PREF_ASSIGNMENT_TIME + "_$uid")

    override fun setStringHonkaiSrExpeditionTime(uid: String, value: String) =
        setString(context, Constant.PREF_ASSIGNMENT_TIME + "_$uid", value)

    override fun getStringRecentSyncTime(uid: String): String =
        getString(context, Constant.PREF_RECENT_SYNC_TIME + "_$uid")

    override fun setStringRecentSyncTime(uid: String, value: String) =
        setString(context, Constant.PREF_RECENT_SYNC_TIME + "_$uid", value)


    override fun getStringRecentDailyCommissionNotiDate(uid: String): String =
        getString(context, Constant.PREF_RECENT_DAILY_COMMISSION_NOTI_DATE + "_$uid")

    override fun setStringRecentDailyCommissionNotiDate(uid: String, value: String) =
        setString(context, Constant.PREF_RECENT_DAILY_COMMISSION_NOTI_DATE + "_$uid", value)

    override fun getStringRecentWeeklyBossNotiDate(uid: String): String =
        getString(context, Constant.PREF_RECENT_WEEKLY_BOSS_NOTI_DATE + "_$uid")

    override fun setStringRecentWeeklyBossNotiDate(uid: String, value: String) =
        setString(context, Constant.PREF_RECENT_WEEKLY_BOSS_NOTI_DATE + "_$uid", value)


    override fun getStringLocale(): String =
        getString(context, Constant.PREF_LOCALE, Locale.getDefault().language)

    override fun setStringLocale(value: String) =
        setString(context, Constant.PREF_LOCALE, value)


    /* Deprecated */

    @Deprecated("Isolated by Permission Check")
    override fun getBooleanFirstLaunch(): Boolean =
        getBoolean(context, Constant.PREF_CHECKED_STORAGE_PERMISSION, true)

    @Deprecated("Isolated by Permission Check")
    override fun setBooleanFirstLaunch(value: Boolean) =
        setBoolean(context, Constant.PREF_CHECKED_STORAGE_PERMISSION, value)


    @Deprecated("Room migration")
    override fun getIntServer(): Int = getInt(context, Constant.PREF_SERVER)

    @Deprecated("Room migration")
    override fun setIntServer(value: Int) = setInt(context, Constant.PREF_SERVER, value)

    @Deprecated("Room migration")
    override fun getStringUid(): String = getString(context, Constant.PREF_UID)

    @Deprecated("Room migration")
    override fun setStringUid(value: String) = setString(context, Constant.PREF_UID, value)

    @Deprecated("Room migration")
    override fun getStringCookie(): String = getString(context, Constant.PREF_COOKIE)

    @Deprecated("Room migration")
    override fun setStringCookie(value: String) = setString(context, Constant.PREF_COOKIE, value)


    @Deprecated("Room migration")
    override fun setBooleanEnableAutoCheckIn(value: Boolean) =
        setBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, value)

    @Deprecated("Room migration")
    override fun setBooleanEnableHonkai3rdAutoCheckIn(value: Boolean) =
        setBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, value)

    @Deprecated("Room migration")
    override fun getBooleanCheckedUpdateNote(): Boolean =
        getBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, false)

    @Deprecated("Room migration")
    override fun setBooleanCheckedUpdateNote(value: Boolean) =
        setBoolean(context, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, value)

    @Deprecated("Room migration")
    override fun getBooleanIsValidUserData(): Boolean =
        getBoolean(context, Constant.PREF_IS_VALID_USERDATA, false)

    @Deprecated("Room migration")
    override fun setBooleanIsValidUserData(value: Boolean) =
        setBoolean(context, Constant.PREF_IS_VALID_USERDATA, value)
}