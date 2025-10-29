package danggai.app.presentation.ui.design

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.charaters.WidgetDesignCharacterFragment
import danggai.app.presentation.ui.design.charaters.select.WidgetDesignSelectCharacterFragment
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.ui.design.resin.WidgetDesignResinFragment
import danggai.app.presentation.ui.widget.BatteryWidget
import danggai.app.presentation.ui.widget.DetailWidget
import danggai.app.presentation.ui.widget.HKSRDetailWidget
import danggai.app.presentation.ui.widget.ResinWidget
import danggai.app.presentation.ui.widget.TrailPowerWidget
import danggai.app.presentation.ui.widget.ZZZDetailWidget
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.WidgetUtils
import danggai.app.presentation.util.log
import danggai.domain.local.DesignTabType
import danggai.domain.local.Preview
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetDesignFragment : BindingFragment<FragmentWidgetDesignBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignFragment::class.java.simpleName
        fun newInstance() = WidgetDesignFragment()
    }

    private val mVM: WidgetDesignViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM.apply {
            initUi()
            setCommonFun()
        }

        initTabLayout()

        initSf()
    }

    private fun initTabLayout() {
        val pagerAdapter = WidgetDesignAdapter(requireActivity()).apply {
            DesignTabType.values()
                .sortedBy { it.position } // position 기준으로 정렬
                .forEach { tabType ->
                    when (tabType) {
                        DesignTabType.STAMINA -> addFragment(WidgetDesignResinFragment())
                        DesignTabType.DETAIL -> addFragment(WidgetDesignDetailFragment())
                        DesignTabType.TALENT -> addFragment(WidgetDesignCharacterFragment())
                        DesignTabType.UNKNOWN -> { /* 아무것도 하지 않음 */
                        }
                    }
                }
        }

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlTop, binding.vpMain) { tab, position ->
            tab.text = DesignTabType.fromPosition(position).title
        }.attach()
    }

    private fun <T : AppWidgetProvider> requestPinWidget(context: Context, widgetClass: Class<T>) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val widgetProvider = ComponentName(context, widgetClass)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                appWidgetManager.requestPinAppWidget(
                    widgetProvider,
                    null,
                    null
                )
            } else {
                makeToast(context, context.getString(R.string.msg_toast_widget_pin_not_supported))
            }
        else {
            makeToast(context, context.getString(R.string.msg_toast_widget_pin_supports_android_8))
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfApplySavedData.collect {
                    context?.let { _context ->
                        log.e()
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(_context)
                        _context.sendBroadcast(WidgetUtils.getTalentRefreshIntent(_context))
                    }
                }
            }

            launch {
                mVM.sfAddWidget.collect { preview ->
                    val context = requireContext()
                    val designTab = DesignTabType.fromPosition(binding.vpMain.currentItem)

                    // 위젯 매핑 테이블 (디자인 탭 + 프리뷰에 따른 위젯 매핑)
                    val widgetMap = mapOf(
                        DesignTabType.STAMINA to mapOf(
                            Preview.GENSHIN to ResinWidget::class.java,
                            Preview.STARRAIL to TrailPowerWidget::class.java,
                            Preview.ZZZ to BatteryWidget::class.java
                        ),
                        DesignTabType.DETAIL to mapOf(
                            Preview.GENSHIN to DetailWidget::class.java,
                            Preview.STARRAIL to HKSRDetailWidget::class.java,
                            Preview.ZZZ to ZZZDetailWidget::class.java
                        )
                    )

                    val widgetClass = widgetMap[designTab]?.get(preview)


                    if (widgetClass !== null)
                        requestPinWidget(context, widgetClass)
                }
            }

            launch {
                mVM.sfStartSelectFragment.collect {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_up,
                            R.anim.slide_out_down,
                            R.anim.slide_in_up,
                            R.anim.slide_out_down
                        )
                        .add(
                            R.id.fragment,
                            WidgetDesignSelectCharacterFragment.newInstance(),
                            WidgetDesignSelectCharacterFragment.TAG
                        )
                        .commit()
                }
            }
        }
    }
}