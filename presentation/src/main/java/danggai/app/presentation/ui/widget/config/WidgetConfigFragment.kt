package danggai.app.presentation.ui.widget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetConfigBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WidgetConfigFragment : BindingFragment<FragmentWidgetConfigBinding, WidgetConfigViewModel>() {

    companion object {
        val TAG: String = WidgetConfigFragment::class.java.simpleName
        fun newInstance() = WidgetConfigFragment()
    }

    var widgetClassName = ""
    var appWidgetId = 0
    var views: RemoteViews? = null
    var appWidgetManager: AppWidgetManager? = null

    private val mVM: WidgetConfigViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_config

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.setResult(Activity.RESULT_CANCELED)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        appWidgetId = getIntent().extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        appWidgetManager = AppWidgetManager.getInstance(context).apply {
            widgetClassName = getAppWidgetInfo(appWidgetId).provider.className
        }

        views = RemoteViews(context?.packageName, R.layout.widget_detail_fixed)
        appWidgetManager?.updateAppWidget(appWidgetId, views)

        initSf()
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                activity?.let { act ->
                    mVM.sfSelectAccount.collect { account ->
                        log.e()

                        val widgetClass = Class.forName(widgetClassName)

                        val updateIntent = Intent(act.applicationContext, widgetClass).apply {
                            action = Constant.ACTION_RESIN_WIDGET_REFRESH_DATA
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            putExtra("uid", account.genshin_uid)
                        }
                        act.sendBroadcast(updateIntent)

                        val resultValue = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }

                        act.setResult(Activity.RESULT_OK, resultValue)
                        act.finish()
                    }

                }
            }
            launch {
                activity?.let { act ->
                    mVM.sfNoAccount.collect { account ->
                        log.e()
                        makeToast(act.applicationContext, getString(R.string.msg_toast_widget_no_account))

                        activity?.setResult(Activity.RESULT_CANCELED)
                        activity?.finish()
                    }

                }
            }
        }
    }
}