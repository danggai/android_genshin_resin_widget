package danggai.app.presentation.core.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder


object PreferenceManager {
    private const val PREFERENCES_NAME = "danggai.app.resinwidget"
    private const val DEFAULT_VALUE_STRING = ""
    private const val DEFAULT_VALUE_BOOLEAN = false
    private const val DEFAULT_VALUE_INT = -1
    private const val DEFAULT_VALUE_LONG = -1L
    private const val DEFAULT_VALUE_FLOAT = -1f

    fun getPreferences(context: Context): SharedPreferences {
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
}