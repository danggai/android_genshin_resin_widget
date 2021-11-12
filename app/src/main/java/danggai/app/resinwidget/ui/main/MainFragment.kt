package danggai.app.resinwidget.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import danggai.app.resinwidget.BuildConfig
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.MainFragmentBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.cookie_web_view.CookieWebViewActivity
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
        initLv()
        
        if (!BuildConfig.DEBUG) initAd()
    }

    fun onNewIntent(intent: Intent?) {
        intent?.let {
            try {
                log.e()
                val cookie = it.getStringExtra(MainActivity.ARG_PARAM_COOKIE)!!
                mVM.lvCookie.value = cookie
            } catch (e: Exception) {
                log.e(e.toString())
            }
        }
    }

    private fun initUi() {
        context?.let {
            mVM.lvAutoRefreshPeriod.value = PreferenceManager.getLongAutoRefreshPeriod(it)

            mVM.lvEnableNotiEach40Resin.value = PreferenceManager.getBooleanNotiEach40Resin(it)
            mVM.lvEnableNoti140Resin.value = PreferenceManager.getBooleanNoti140Resin(it)
            mVM.lvEnableNotiCustomResin.value = PreferenceManager.getBooleanNotiCustomResin(it)
            mVM.lvCustomNotiResin.value = PreferenceManager.getIntCustomTargetResin(it).let { int ->
                if (int == -1) "0" else int.toString()
            }
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

                PreferenceManager.setBooleanNotiEach40Resin(it, mVM.lvEnableNotiEach40Resin.value)
                PreferenceManager.setBooleanNoti140Resin(it, mVM.lvEnableNoti140Resin.value)
                PreferenceManager.setBooleanNotiCustomResin(it, mVM.lvEnableNotiCustomResin.value)
                val customNotiResin: Int = try {
                    if (mVM.lvCustomNotiResin.value.isEmpty()
                        || mVM.lvCustomNotiResin.value.toInt() < 0) 0
                    else if (mVM.lvCustomNotiResin.value.toInt() > dailyNote.max_resin) {
                        mVM.lvCustomNotiResin.value = dailyNote.max_resin.toString()
                        dailyNote.max_resin.toInt()
                    } else mVM.lvCustomNotiResin.value.toInt()
                } catch (e:java.lang.Exception) {
                    0
                }
                PreferenceManager.setIntCustomTargetResin(it, customNotiResin)

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

        mVM.lvWidgetRefreshNotWork.observe(viewLifecycleOwner, EventObserver {
            activity?.let {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_widget_refresh_not_work)
                    .setMessage(R.string.dialog_msg_widget_refresh_not_work)
                    .setPositiveButton(R.string.data_save_mode_disable) { dialog, whichButton ->
                        log.e()
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            makeToast(requireContext(), getString(R.string.data_save_mode_not_supported))
                        } else {
                            try {
                                startActivity(Intent("android.settings.DATA_USAGE_SETTINGS"))
                            } catch (e: Exception) {
                                log.e(e.message.toString())

                                AlertDialog.Builder(requireActivity())
                                    .setMessage(R.string.msg_toast_data_save_mode)
                                    .setCancelable(false)
                                    .setPositiveButton("OK") { dialog, whichButton ->
                                        startActivity(Intent("android.settings.WIRELESS_SETTINGS"))
                                    }
                                    .show()
                            }
                        }
                    }
                    .setNegativeButton(R.string.permission_accept) { dialog, whichButton ->
                        log.e()
                        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                    }
                    .create()
                    .show()
            }
        })

        mVM.lvHowCanIGetCookie.observe(viewLifecycleOwner, EventObserver {
            activity?.let {
                log.e()
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_native_hoyolab_account)
                    .setMessage(R.string.dialog_msg_native_hoyolab_account)
                    .setPositiveButton(R.string.native_hoyolab_account) { dialog, whichButton ->
                        log.e()
                        CookieWebViewActivity.startActivity(requireActivity())
                    }
                    .setNegativeButton(R.string.sns_account) { dialog, whichButton ->
                        log.e()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_how_can_i_get_cookie)))
                        startActivity(intent)
                    }
                    .create()
                    .show()
            }
        })

        mVM.lvWhenDailyNotePrivate.observe(viewLifecycleOwner, EventObserver {
            activity?.let {
                log.e()
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_realtime_note_private)
                    .setMessage(R.string.dialog_msg_realtime_note_private)
                    .setCancelable(false)
                    .setPositiveButton(R.string.apply) { dialog, whichButton ->
                        log.e()
                        mVM.makeDailyNotePublic()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                        log.e()
                        makeToast(requireContext(), getString(R.string.msg_toast_dailynote_error_data_not_public))
                    }
                    .create()
                    .show()
            }
        })

        mVM.lvSetProgress.observe(viewLifecycleOwner, EventObserver {
            log.e()
            binding.llProgress.visibility = if (it) View.VISIBLE else View.GONE
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