package danggai.app.presentation.util

import android.util.Log
import danggai.app.presentation.BuildConfig

object log {

    val TAG = "myLog"

    fun d () {
        val e = Exception()
        val element: Array<StackTraceElement> = e.stackTrace

        if (BuildConfig.DEBUG)
            Log.d(TAG, "(" + element[1].fileName + ":" + element[1].lineNumber + ") " +  element[1].methodName)
    }

    fun d (msg: Any) {
        val e = Exception()
        val element: Array<StackTraceElement> = e.stackTrace

        if (BuildConfig.DEBUG)
            Log.d(TAG, "(" + element[1].fileName + ":" + element[1].lineNumber + ") " +  element[1].methodName + ": " + msg.toString())
    }

    fun e () {
        val e = Exception()
        val element: Array<StackTraceElement> = e.stackTrace

        if (BuildConfig.DEBUG)
            Log.e(TAG, "(" + element[1].fileName + ":" + element[1].lineNumber + ") " +  element[1].methodName)
    }

    fun e (msg: Any) {
        val e = Exception()
        val element: Array<StackTraceElement> = e.stackTrace

        if (BuildConfig.DEBUG)
            Log.e(TAG, "(" + element[1].fileName + ":" + element[1].lineNumber + ") " +  element[1].methodName + ": " + msg.toString())
    }
}