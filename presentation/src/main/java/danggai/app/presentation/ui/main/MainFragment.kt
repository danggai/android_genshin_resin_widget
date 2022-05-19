package danggai.app.presentation.ui.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.BuildConfig
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentMainBinding
import danggai.app.presentation.ui.cookie.CookieWebViewActivity
import danggai.app.presentation.ui.design.WidgetDesignActivity
import danggai.app.presentation.ui.main.checkin.MainCheckInFragment
import danggai.app.presentation.ui.main.resin.MainResinFragment
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

    fun onNewIntent(intent: Intent?) {
        intent?.let {
            try {
                log.e()
                val cookie = it.getStringExtra(MainActivity.ARG_PARAM_COOKIE)!!
                mVM.sfCookie.value = cookie
            } catch (e: Exception) {
                log.e(e.toString())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        initTabLayout()

        migrateCheck()

        if (!BuildConfig.DEBUG)
            initAd()

        initUi()

        permissionCheck()

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
    }

    private fun permissionCheck() {
        context?.let { it ->
            if (PreferenceManager.getBoolean(it, Constant.PREF_FIRST_LAUNCH, true)) {
                log.e()

                val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
                    PreferenceManager.setBoolean(it, Constant.PREF_FIRST_LAUNCH, false)
                    if (isGranted) {
                        log.e()
                    } else {
                        log.e()
                    }
                }

                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_get_storage_permission)
                    .setMessage(R.string.dialog_msg_get_storage_permission)
                    .setPositiveButton(R.string.apply) { dialog, whichButton ->
                        log.e()
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun initTabLayout() {
        val pagerAdapter = MainAdapter(requireActivity())

        pagerAdapter.addFragment(MainResinFragment())
        pagerAdapter.addFragment(MainCheckInFragment())

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlTop, binding.vpMain) { tab, position ->
            tab.text = when (position) {
                0 -> "Resin Widget"
                1 -> "HoYoLAB Check In"
                else -> ""
            }
        }.attach()
    }

    private fun updateNoteCheck() {
        context?.let { it ->

            if (!PreferenceManager.getBoolean(it, Constant.PREF_CHECKED_UPDATE_NOTE + BuildConfig.VERSION_NAME, false)) {
                if (!PreferenceManager.getBoolean(it, Constant.PREF_FIRST_LAUNCH, true)) {
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
            is Event.WhenDailyNoteIsPrivate -> {
                activity?.let {
                    log.e()
                    if (mVM.dailyNotePrivateErrorCount >= 2) {
                        AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.dialog_realtime_note_private)
                            .setMessage(R.string.dialog_msg_dailynote_not_public_error_repeat)
                            .setCancelable(false)
                            .setPositiveButton(R.string.apply) { dialog, whichButton ->
                                log.e()
                            }
                            .create()
                            .show()
                    }

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
            }
            is Event.StartWidgetDesignActivity -> {
                log.e()
                activity?.let {
                    WidgetDesignActivity.startActivity(it)
                }
            }
            is Event.ChangeLanguage -> {
                log.e()
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle("Select Language")
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