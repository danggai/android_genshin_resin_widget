package danggai.app.resinwidget.util

import android.content.Context
import android.content.SharedPreferences
import danggai.app.resinwidget.Constant


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
        return prefs.getString(key, DEFAULT_VALUE_STRING)?:DEFAULT_VALUE_STRING
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

    fun getStringRecentSyncTime(context: Context): String {
        return getString(context, Constant.PREF_RECENT_SYNC_TIME)
    }
    fun setStringRecentSyncTime(context: Context, value: String) {
        setString(context, Constant.PREF_RECENT_SYNC_TIME, value)
    }




}