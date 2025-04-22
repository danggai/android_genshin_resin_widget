package danggai.app.presentation.ui.widget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetConfigBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.widget.MiniWidget
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WidgetConfigFragment : BindingFragment<FragmentWidgetConfigBinding, WidgetConfigViewModel>() {

    companion object {
        val TAG: String = WidgetConfigFragment::class.java.simpleName
        fun newInstance() = WidgetConfigFragment()
    }


    private val appWidgetId by lazy {
        getIntent().extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private val appWidgetManager: AppWidgetManager by lazy {
        AppWidgetManager.getInstance(context)
    }

    private val widgetClassName by lazy {
        appWidgetManager.getAppWidgetInfo(appWidgetId).provider.className
    }

    var views: RemoteViews? = null

    private val mVM: WidgetConfigViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_config

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.setResult(Activity.RESULT_CANCELED)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM.apply {
            setCommonFun()
            this.widgetClassName = this@WidgetConfigFragment.widgetClassName
        }

        // appwidgetid 없는 widget 삭제 지연을 위해
        context?.let {
            PreferenceManager.setString(it, Constant.PREF_UID + "_$appWidgetId", "")
        }

        views = RemoteViews(context?.packageName, R.layout.widget_detail_fixed)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        val listener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            mVM.onClickRoundButton(checkedId)
        }
        binding.rgMiniWidgetType.setOnCheckedChangeListener(listener)
        binding.rgSelectedChara.setOnCheckedChangeListener(listener)

        initUi()
        initSf()
    }

    private fun initUi() {
        binding.llMiniSetting.visibility =
            if (widgetClassName == MiniWidget::class.java.name) View.VISIBLE
            else View.GONE

        binding.llTalentSetting.visibility =
            if (widgetClassName == TalentWidget::class.java.name) View.VISIBLE
            else View.GONE

        binding.rvAccounts.visibility =
            if (widgetClassName == TalentWidget::class.java.name) View.GONE
            else View.VISIBLE
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
                            putExtra("uid", mVM.getUid(account))
                            putExtra("name", mVM.getNickname(account))
                            if (widgetClassName == MiniWidget::class.java.name || widgetClassName == TalentWidget::class.java.name)
                                putExtra("paramType", mVM.widgetType)
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
                        if (widgetClassName == TalentWidget::class.java.name) return@collect

                        log.e()
                        makeToast(
                            act.applicationContext,
                            getString(R.string.msg_toast_widget_no_account)
                        )

                        activity?.setResult(Activity.RESULT_CANCELED)
                        activity?.finish()
                    }
                }
            }
        }
    }
}