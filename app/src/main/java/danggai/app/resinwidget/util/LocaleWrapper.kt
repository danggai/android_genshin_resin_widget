package danggai.app.resinwidget.util

import android.content.Context
import java.util.*

/*
* sourced by https://github.com/rimduhui/android-different-languages/blob/master/app/src/main/java/com/rimduhui/differentlanguages/LocaleWrapper.java
* */


object LocaleWrapper {
    fun wrap(context: Context): Context {
        val sLocale = Locale(PreferenceManager.getStringLocale(context))

        val res = context.resources
        val config = res.configuration
        config.setLocale(sLocale)
        return context.createConfigurationContext(config)
    }

}