package danggai.app.presentation.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import kotlinx.coroutines.launch

open class BaseFragment: Fragment() {

    fun getIntent(): Intent {
        return activity?.intent ?: Intent()
    }

    fun makeToast(context: Context, msg: String) {
        log.e()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun makeToastLong(context: Context, msg: String) {
        log.e()
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun getAppVersion(): String {
        context?.let {
            try {
                val i: PackageInfo = it.packageManager.getPackageInfo(it.packageName, 0)
                return i.versionName
            } catch (e: PackageManager.NameNotFoundException)
            { }
        }

        return ""
    }

    fun BaseViewModel.setCommonFun() {
        lifecycleScope.launch { eventFlow.collect { event -> handleEvents(event) } }
    }

    /**
     * MakeToast와 FinishThisActivity를 사용하고 싶다면
     * 반드시 super method를 override 할 것
     */
    open fun handleEvents(event: Event) {
        log.e(event::class.java.name)

        when (event) {
            is Event.MakeToast -> {
                activity?.let {
                    if (event.message.isNotBlank()) makeToast(it, event.message)
                }
            }
            is Event.FinishThisActivity -> {
                activity?.finish()
            }
            else -> {}
        }
    }
}