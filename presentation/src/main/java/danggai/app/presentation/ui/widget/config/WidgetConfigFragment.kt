package danggai.app.presentation.ui.widget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetConfigBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.widget.DetailWidget
import danggai.app.presentation.util.log
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WidgetConfigFragment : BindingFragment<FragmentWidgetConfigBinding, WidgetConfigViewModel>() {

    companion object {
        val TAG: String = WidgetConfigFragment::class.java.simpleName
        fun newInstance() = WidgetConfigFragment()
    }

    var appWidgetId = 0
    var widgetTitle: EditText? = null
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

        appWidgetManager = AppWidgetManager.getInstance(context)

        views = RemoteViews(context?.packageName, R.layout.widget_detail_fixed)
        appWidgetManager?.updateAppWidget(appWidgetId, views)

        initSf()
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                context?.let { context ->
                    mVM.sfSelectAccount.collect { account ->
                        log.e()

                        val updateIntent = Intent(context, DetailWidget::class.java).apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                            putExtra("uid", account.genshin_uid)
                        }
                        activity?.sendBroadcast(updateIntent)

                        val resultValue = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }

                        activity?.setResult(Activity.RESULT_OK, resultValue)
                        activity?.finish()
                    }

                }
            }
        }
    }
}