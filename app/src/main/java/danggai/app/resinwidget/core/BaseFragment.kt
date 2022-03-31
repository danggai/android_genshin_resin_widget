package danggai.app.resinwidget.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.log

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

    fun BaseViewModel.setCommonFun(view: View) {
        lvMakeToast.observe(viewLifecycleOwner, EventObserver { msg ->
            activity?.let {
                if (msg.isNotBlank()) makeToast(it, msg)
            }
        })
    }

}