package danggai.app.presentation.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.BuildConfig
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentMainBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.cookie.CookieWebViewActivity
import danggai.app.presentation.ui.design.WidgetDesignActivity
import danggai.app.presentation.ui.newaccount.NewHoyolabAccountActivity
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.CheckInWorker
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.util.Constant
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding, MainViewModel>() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_main

    private val mVM: MainViewModel by activityViewModels()
    private lateinit var mAdView : AdView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        migrateCheck()

        if (!BuildConfig.DEBUG)
            initAd()

        initUi()
        initSf()

        context?.let { it ->
            antidozePermisisonCheck(it)
        }

        updateNoteCheck()
    }

    private fun initUi() {
        context?.let { it ->
            mVM.initUI()
        }

        WorkManager.getInstance(requireContext()).getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH).get().forEach {
            if (it.state !in listOf(WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
                log.e("refresh worker ${it.id} state -> ${it.state}")
        }
        WorkManager.getInstance(requireContext()).getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN).get().forEach {
            if (it.state !in listOf(WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
                log.e("checkin worker ${it.id} state -> ${it.state}")
        }
        WorkManager.getInstance(requireContext()).getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH).get().forEach {
            if (it.state !in listOf(WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
                log.e("talent worker ${it.id} state -> ${it.state}")
        }

        when (mVM.sfAutoRefreshPeriod.value) {
            15L -> binding.rb15m.isChecked = true
            30L -> binding.rb30m.isChecked = true
            60L -> binding.rb1h.isChecked = true
            120L -> binding.rb2h.isChecked = true
            else -> binding.rbDisable.isChecked = true
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfDeleteAccount.collect { account ->
                    activity?.let { activity ->
                        AlertDialog.Builder(activity)
                            .setTitle(R.string.dialog_hoyolab_account_delete)
                            .setMessage(
                                String.format(getString(R.string.dialog_msg_hoyolab_account_delete), account.nickname)
                            )
                            .setCancelable(false)
                            .setPositiveButton(R.string.apply) { dialog, whichButton ->
                                log.e()
                                mVM.deleteAccount(account.genshin_uid)
                            }
                            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                                log.e()
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
    }

    private fun antidozePermisisonCheck(context: Context) {
        if (PreferenceManager.getBoolean(context, Constant.PREF_CHECKED_ANTIDOZE_PERMISSION, true)) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_permission)
                .setMessage(R.string.dialog_msg_permission_antidoze)
                .setCancelable(false)
                .setPositiveButton(R.string.apply) { dialog, whichButton ->
                    log.e()
                    PreferenceManager.setBoolean(context, Constant.PREF_CHECKED_ANTIDOZE_PERMISSION, false)

                    val intent = Intent()
                    val packageName = context.packageName
                    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (pm.isIgnoringBatteryOptimizations(packageName)) intent.action =
                        Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS else {
                        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        intent.data = Uri.parse("package:$packageName")
                    }
                    context.startActivity(intent)
                }
                .create()
                .show()
        }
    }

    private fun updateNoteCheck() {
        context?.let { it ->

            if (!PreferenceManager.getBoolean(it, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, false)) {
                if (!PreferenceManager.getBoolean(it, Constant.PREF_CHECKED_STORAGE_PERMISSION, true)) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(String.format(getString(R.string.dialog_patch_note), BuildConfig.VERSION_NAME))
                        .setMessage(R.string.dialog_msg_patch_note)
                        .setPositiveButton(R.string.ok) { dialog, whichButton ->
                            log.e()
                        }
                        .create()
                        .show()
                }

                PreferenceManager.setBoolean(it, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, true)
            }
        }
    }

    private fun migrateCheck() {
        context?.let { context ->
            if (PreferenceManager.getT<DailyNoteData>(context, Constant.PREF_DAILY_NOTE_DATA)?: DailyNoteData.EMPTY == DailyNoteData.EMPTY &&
                PreferenceManager.getT<DailyNoteSettings>(context, Constant.PREF_WIDGET_SETTINGS)?: DailyNoteSettings.EMPTY == DailyNoteSettings.EMPTY &&
                PreferenceManager.getT<CheckInSettings>(context, Constant.PREF_CHECK_IN_SETTINGS)?: CheckInSettings.EMPTY == CheckInSettings.EMPTY &&
                PreferenceManager.getT<ResinWidgetDesignSettings>(context, Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS)?: ResinWidgetDesignSettings.EMPTY == ResinWidgetDesignSettings.EMPTY &&
                PreferenceManager.getT<DetailWidgetDesignSettings>(context, Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS)?: DetailWidgetDesignSettings.EMPTY == DetailWidgetDesignSettings.EMPTY
            ) CommonFunction.migrateSettings(context)
        }
    }

    private fun initAd() {
        log.e()
        MobileAds.initialize(requireContext())

        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun handleEvents(event: Event) {
        super.handleEvents(event)

        when (event) {
            is Event.WidgetRefreshNotWork -> {
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
            }
            is Event.GetCookie -> {
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
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.HOW_CAN_I_GET_COOKIE_URL))
                            startActivity(intent)
                        }
                        .create()
                        .show()
                }
            }
            is Event.StartWidgetDesignActivity -> {
                log.e()
                activity?.let {
                    WidgetDesignActivity.startActivity(it)
                }
            }
            is Event.StartNewHoyolabAccountActivity -> {
                log.e()
                activity?.let {
                    NewHoyolabAccountActivity.startActivity(it)
                }
            }
            is Event.StartManageAccount -> {
                log.e()
                activity?.let {
                    NewHoyolabAccountActivity.startActivityWithUid(it, event.account.genshin_uid)
                }
            }
            is Event.ChangeLanguage -> {
                log.e()
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle("Change Language")
                        .setItems(arrayOf("English", "한국어"),
                            DialogInterface.OnClickListener { dialog, which ->
                                log.e(which)
                                val locale: String = when (which) {
                                    Constant.Locale.ENGLISH.index -> Constant.Locale.ENGLISH.locale
                                    Constant.Locale.KOREAN.index -> Constant.Locale.KOREAN.locale
                                    else -> Locale.getDefault().language
                                }

                                if (PreferenceManager.getString(it.baseContext, Constant.PREF_LOCALE, Locale.getDefault().language) == locale) return@OnClickListener

                                PreferenceManager.setString(it.baseContext, Constant.PREF_LOCALE, locale)

                                AlertDialog.Builder(requireActivity())
                                    .setTitle(R.string.dialog_restart)
                                    .setMessage(R.string.dialog_msg_restart)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.apply) { dialog, whichButton ->
                                        log.e()
                                        CommonFunction.restartApp(it.baseContext)
                                    }
                                    .create()
                                    .show()
                            })
                    builder.show()
                }
            }
            is Event.StartShutRefreshWorker -> {
                log.e()
                context?.let { context ->
                    if (event.isValid) RefreshWorker.startWorkerPeriodic(context)
                    else RefreshWorker.shutdownWorker(context)
                }
            }
            is Event.StartShutCheckInWorker -> {
                log.e()
                context?.let { context ->
                    if (event.isValid) CheckInWorker.startWorkerOneTimeImmediately(context)
                    else CheckInWorker.shutdownWorker(context)
                }
            }
        }
    }
}