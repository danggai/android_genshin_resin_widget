package danggai.app.resinwidget.ui.main

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import danggai.app.resinwidget.BuildConfig
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.MainFragmentBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainFragment : BindingFragment<MainFragmentBinding>() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var mVM: MainViewModel
    private lateinit var mAdView : AdView

    @LayoutRes
    override fun getLayoutResId() = R.layout.main_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = getViewModel()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm?.let {
            mVM = it
            it.setCommonFun(view)
        }

        initUi()
        initAd()
        initLv()
    }

    private fun initUi() {
        context?.let {
            mVM.lvAutoRefreshPeriod.value = PreferenceManager.getLongAutoRefreshPeriod(it)
        }

        when(mVM.lvAutoRefreshPeriod.value) {
            -1L -> binding.rbDisable.isChecked = true
            15L -> binding.rb15m.isChecked = true
            30L -> binding.rb30m.isChecked = true
            60L -> binding.rb1h.isChecked = true
            120L -> binding.rb2h.isChecked = true
        }

        context?.let { it ->
            mVM.initUI(PreferenceManager.getStringUid(it), PreferenceManager.getStringCookie(it))
        }
    }

    private fun initLv() {
        mVM.lvSaveUserInfo.observe(viewLifecycleOwner, EventObserver { boolean ->
            context?.let { _context ->
                if (boolean) {
                    log.e()
                    PreferenceManager.setStringUid(_context, mVM.lvUid.value)
                    PreferenceManager.setStringCookie(_context, mVM.lvCookie.value)

                    makeToast(_context, getString(R.string.msg_toast_save_done))

                } else if (!boolean and mVM.lvUid.value.isEmpty()) {

                    makeToast(_context, getString(R.string.msg_toast_uid_empty_error))
                } else if (!boolean and mVM.lvCookie.value.isEmpty()) {

                    makeToast(_context, getString(R.string.msg_toast_cookie_empty_error))
                }
            }
        })

        mVM.lvSendWidgetSyncBroadcast.observe(viewLifecycleOwner, EventObserver { dailyNote ->
            context?.let { it ->
                log.e()
                PreferenceManager.setBooleanIsValidUserData(it, true)

                PreferenceManager.setIntCurrentResin(it, dailyNote.current_resin)
                PreferenceManager.setIntMaxResin(it, dailyNote.max_resin)
                PreferenceManager.setStringResinRecoveryTime(it, dailyNote.resin_recovery_time?:"-1")
                PreferenceManager.setStringRecentSyncTime(it, CommonFunction.getTimeSyncTimeFormat())

                requireActivity().sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
                CommonFunction.startUniquePeriodicRefreshWorker(it)
            }
        })

        mVM.lvSetAutoRefreshPeriod.observe(viewLifecycleOwner, EventObserver { period ->
            activity?.let { act ->
                log.e(period)
                PreferenceManager.setLongAutoRefreshPeriod(act, period)
            }
        })
    }

    private fun initAd() {
        log.e()
        MobileAds.initialize(requireContext())

        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

}