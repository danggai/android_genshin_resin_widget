package danggai.app.presentation.util

import android.content.Context
import danggai.domain.util.Constant
import java.util.Locale

/*
* sourced by https://github.com/rimduhui/android-different-languages/blob/master/app/src/main/java/com/rimduhui/differentlanguages/LocaleWrapper.java
* */


object LocaleWrapper {
    fun wrap(context: Context): Context {
        val sLocale = Locale(
            PreferenceManager.getString(
                context,
                Constant.PREF_LOCALE,
                Locale.getDefault().language
            )
        )

        val res = context.resources
        val config = res.configuration
        config.setLocale(sLocale)
        return context.createConfigurationContext(config)
    }
}