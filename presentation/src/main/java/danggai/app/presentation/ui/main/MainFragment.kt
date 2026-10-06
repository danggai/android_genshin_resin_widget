package danggai.app.presentation.ui.main

import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
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
import danggai.app.presentation.ui.widget.BatteryWidget
import danggai.app.presentation.ui.widget.DetailWidget
import danggai.app.presentation.ui.widget.HKSRDetailWidget
import danggai.app.presentation.ui.widget.MiniWidget
import danggai.app.presentation.ui.widget.ResinWidget
import danggai.app.presentation.ui.widget.ResinWidgetResizable
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.ui.widget.TrailPowerWidget
import danggai.app.presentation.ui.widget.ZZZDetailWidget
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.DayTimeMapper
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.CheckInWorker
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.util.Constant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding, MainViewModel>() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_main

    private val mVM: MainViewModel by activityViewModels()
    private lateinit var mAdView: AdView

    private val weeklyDaySpinnerAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.week,
            R.layout.text_spinner
        ).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spWeeklyYetNotiDay.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        mVM.setWeeklyCommissionNotiDay(
                            binding.spWeeklyYetNotiDay.getItemAtPosition(
                                position
                            ) as String
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    private val weeklyTimeSpinnerAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_oclock,
            R.layout.text_spinner
        ).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spWeeklyYetNotiTime.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        mVM.setWeeklyCommissionNotiTime(
                            binding.spWeeklyYetNotiTime.getItemAtPosition(
                                position
                            ) as String
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    private val dailyTimeSpinnerAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_oclock,
            R.layout.text_spinner
        ).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spDailyYetNoti.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        mVM.setDailyCommissionNotiTime(
                            binding.spDailyYetNoti.getItemAtPosition(
                                position
                            ) as String
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        if (!BuildConfig.DEBUG)
            initAd()

        initSf()
        initUi()

        notificationPermisisonCheck()
        antidozePermisisonCheck(view.context)

        updateNoteCheck()
    }

    private fun initUi() {
        context?.let {
            mVM.initUI()
        }

        WorkManager.getInstance(requireContext())
            .getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH).get().forEach {
                if (it.state !in listOf(
                        WorkInfo.State.SUCCEEDED,
                        WorkInfo.State.FAILED,
                        WorkInfo.State.CANCELLED
                    )
                )
                    log.e("refresh worker ${it.id} state -> ${it.state}")
            }
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN).get().forEach {
                if (it.state !in listOf(
                        WorkInfo.State.SUCCEEDED,
                        WorkInfo.State.FAILED,
                        WorkInfo.State.CANCELLED
                    )
                )
                    log.e("checkin worker ${it.id} state -> ${it.state}")
            }
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTag(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH).get().forEach {
                if (it.state !in listOf(
                        WorkInfo.State.SUCCEEDED,
                        WorkInfo.State.FAILED,
                        WorkInfo.State.CANCELLED
                    )
                )
                    log.e("talent worker ${it.id} state -> ${it.state}")
            }

        // Adapter, Selection 순으로 적용해야 초기 값이 적용 됨
        binding.spWeeklyYetNotiDay.adapter = weeklyDaySpinnerAdapter
        binding.spWeeklyYetNotiDay.setSelection(
            weeklyDaySpinnerAdapter.getPosition(
                DayTimeMapper.weekOfDayIntToString(requireContext(), mVM.sfNotiWeeklyYetDay.value)
            )
        )

        binding.spWeeklyYetNotiTime.adapter = weeklyTimeSpinnerAdapter
        binding.spWeeklyYetNotiTime.setSelection(
            weeklyTimeSpinnerAdapter.getPosition(
                DayTimeMapper.timeIntToString(requireContext(), mVM.sfNotiWeeklyYetTime.value)
            )
        )

        binding.spDailyYetNoti.adapter = dailyTimeSpinnerAdapter
        binding.spDailyYetNoti.setSelection(
            dailyTimeSpinnerAdapter.getPosition(
                DayTimeMapper.timeIntToString(requireContext(), mVM.sfNotiDailyYetTime.value)
            )
        )

        setUpTimeSpinner(binding.spDailyYetNotiHonkaiSr, mVM.sfNotiDailyYetTimeHonkaiSr)
        setUpDaySpinner(binding.spWeeklyYetNotiDayHonkaiSr, mVM.sfNotiWeeklyYetDayHonkaiSr)
        setUpTimeSpinner(binding.spWeeklyYetNotiTimeHonkaiSr, mVM.sfNotiWeeklyYetTimeHonkaiSr)

        setUpTimeSpinner(binding.spDailyYetNotiZzz, mVM.sfNotiDailyYetTimeZZZ)
        setUpDaySpinner(binding.spWeeklyYetNotiDayZzz, mVM.sfNotiWeeklyYetDayZZZ)
        setUpTimeSpinner(binding.spWeeklyYetNotiTimeZzz, mVM.sfNotiWeeklyYetTimeZZZ)

        setUpDaySpinner(binding.spPeriodScoreDayHonkaiSr, mVM.sfNotiPeriodScoreDayHonkaiSr)
        setUpTimeSpinner(binding.spPeriodScoreTimeHonkaiSr, mVM.sfNotiPeriodScoreTimeHonkaiSr)
        setUpTimeSpinner(binding.spMemberClaimNotiTimeZzz, mVM.sfNotiMemberClaimTimeZZZ)
        setUpExpireDaysSpinner(binding.spMemberExpireNotiDaysZzz, mVM.sfNotiMemberExpireDaysZZZ)
        setUpTimeSpinner(binding.spCafeNotiTimeZzz, mVM.sfNotiCafeTimeZZZ)
    }

    private fun setUpExpireDaysSpinner(spinner: AppCompatSpinner, daysFlow: MutableStateFlow<Int>) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expire_days,
            R.layout.text_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val daysOptions = listOf(1, 3, 7)
        spinner.adapter = adapter
        spinner.setSelection(daysOptions.indexOf(daysFlow.value).coerceAtLeast(0))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                daysFlow.value = daysOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setUpTimeSpinner(spinner: AppCompatSpinner, timeFlow: MutableStateFlow<Int>) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_oclock,
            R.layout.text_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Adapter, Selection 순으로 적용해야 초기 값이 적용 됨
        spinner.adapter = adapter
        spinner.setSelection(
            adapter.getPosition(DayTimeMapper.timeIntToString(requireContext(), timeFlow.value))
        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                timeFlow.value = mVM.timeStringToInt(spinner.getItemAtPosition(position) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setUpDaySpinner(spinner: AppCompatSpinner, dayFlow: MutableStateFlow<Int>) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.week,
            R.layout.text_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
        spinner.setSelection(
            adapter.getPosition(DayTimeMapper.weekOfDayIntToString(requireContext(), dayFlow.value))
        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                dayFlow.value = mVM.weekOfDayStringToInt(spinner.getItemAtPosition(position) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                                String.format(
                                    getString(R.string.dialog_msg_hoyolab_account_delete),
                                    account.nickname
                                )
                            )
                            .setCancelable(false)
                            .setPositiveButton(R.string.apply) { dialog, whichButton ->
                                log.e()
                                mVM.deleteAccount(account)
                            }
                            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                                log.e()
                            }
                            .create()
                            .show()
                    }
                }
            }

            launch {
                mVM.sfShowDialogDailyWeeklyYet.collect { isDaily -> // true= daily, false= weekly
                    activity?.let { activity ->
                        AlertDialog.Builder(activity)
                            .setTitle(R.string.dialog_daily_weekly_yet_noti)
                            .setMessage(getString(R.string.dialog_msg_daily_weekly_yet_noti))
                            .setCancelable(false)
                            .setPositiveButton(R.string.apply) { _, _ ->
                                log.e()
                                if (isDaily) mVM.sfEnableNotiDailyYet.value = true
                                else mVM.sfEnableNotiWeeklyYet.value = true
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                log.e()
                                if (isDaily) mVM.sfEnableNotiDailyYet.value = false
                                else mVM.sfEnableNotiWeeklyYet.value = false
                            }
                            .create()
                            .show()
                    }
                }
            }

            launch {
                mVM.sfShowDialogCustomNoti.collect { showCustomNotiDialog(it) }
            }
            launch {
                mVM.sfNotiWeeklyYetDay.collect {
                    binding.spWeeklyYetNotiDay.setSelection(
                        weeklyDaySpinnerAdapter.getPosition(
                            DayTimeMapper.timeIntToString(requireContext(), it)
                        ), true
                    )
                }
            }

            launch {
                mVM.sfNotiWeeklyYetTime.collect {
                    binding.spWeeklyYetNotiTime.setSelection(
                        weeklyTimeSpinnerAdapter.getPosition(
                            DayTimeMapper.weekOfDayIntToString(requireContext(), it)
                        ), true
                    )
                }
            }

            launch {
                mVM.sfNotiDailyYetTime.collect {
                    binding.spDailyYetNoti.setSelection(
                        weeklyDaySpinnerAdapter.getPosition(
                            DayTimeMapper.timeIntToString(requireContext(), it)
                        ), true
                    )
                }
            }
        }
    }

    private fun antidozePermisisonCheck(context: Context) {
        if (PreferenceManager.getBoolean(
                context,
                Constant.PREF_CHECKED_ANTIDOZE_PERMISSION,
                true
            )
        ) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_permission)
                .setMessage(R.string.dialog_msg_permission_antidoze)
                .setCancelable(false)
                .setPositiveButton(R.string.apply) { dialog, whichButton ->
                    log.e()
                    PreferenceManager.setBoolean(
                        context,
                        Constant.PREF_CHECKED_ANTIDOZE_PERMISSION,
                        false
                    )

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

    private fun isNotificationEnabled(): Boolean {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) true
        else notificationManager.areNotificationsEnabled()
    }

    private fun notificationPermisisonCheck() {
        log.e()
        val currentTime = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationEnabled()) {
                val currentCheckedDate = PreferenceManager.getLong(
                    requireContext(),
                    Constant.PREF_LAST_NOTIFICATION_PERMISSION_CHECK,
                    0
                )

                if (!CommonFunction.isSameDay(currentCheckedDate, currentTime))
                    activity?.let { activity ->
                        AlertDialog.Builder(activity)
                            .setTitle(R.string.dialog_no_noti_permission)
                            .setMessage(getString(R.string.dialog_msg_no_noti_permission))
                            .setCancelable(false)
                            .setPositiveButton(R.string.apply) { dialog, whichButton ->
                                log.e()
                                // 알림 권한이 없는 경우 권한 요청
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                intent.putExtra(
                                    Settings.EXTRA_APP_PACKAGE,
                                    requireContext().packageName
                                )
                                startActivity(intent)
                            }
                            .setNegativeButton(R.string.not_show_today) { dialog, whichButton ->
                                log.e()
                                PreferenceManager.setLong(
                                    requireContext(),
                                    Constant.PREF_LAST_NOTIFICATION_PERMISSION_CHECK,
                                    currentTime
                                )
                            }
                            .create()
                            .show()
                    }
            } else {
                log.e("Permission already granted")
            }
        } else {
            log.e("No need to grant Permission (Android below Tiramisu)")
        }
    }

    private fun updateNoteCheck() {
        context?.let { it ->
            if (PreferenceManager.getString(
                    it,
                    Constant.PREF_CHECKED_UPDATE_NOTE
                ) != BuildConfig.VERSION_NAME
            ) {
                if (!PreferenceManager.getBoolean(
                        it,
                        Constant.PREF_CHECKED_STORAGE_PERMISSION,
                        true
                    )
                ) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(
                            String.format(
                                getString(R.string.dialog_patch_note),
                                BuildConfig.VERSION_NAME
                            )
                        )
                        .setMessage(R.string.dialog_msg_patch_note)
                        .setPositiveButton(R.string.ok) { dialog, whichButton ->
                            log.e()
                        }
                        .create()
                        .show()
                }

                PreferenceManager.setString(
                    it,
                    Constant.PREF_CHECKED_UPDATE_NOTE,
                    BuildConfig.VERSION_NAME
                )
            }
        }
    }

    private fun initAd() {
        log.e()
        MobileAds.initialize(requireContext())

        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onPause() {
        super.onPause()
        mVM.saveIfChanged()
    }

    private fun showCustomNotiDialog(type: CustomNotiType) {
        val context = requireContext()

        val unitRes = when (type) {
            CustomNotiType.RESIN -> R.string.resin
            CustomNotiType.TRAIL_POWER -> R.string.trailblaze_power
            CustomNotiType.BATTERY -> R.string.battery
        }

        val editText = AppCompatEditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(4))
            hint = type.max.toString()
            setText(mVM.getCustomNotiValue(type))
            selectAll()
            setPadding(60, 30, 60, 30)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.dialog_title_custom_noti, getString(unitRes)))
            .setView(editText)
            .setPositiveButton(R.string.ok) { _, _ ->
                mVM.confirmCustomNoti(type, editText.text.toString())
            }
            .setNegativeButton(R.string.cancel) { _, _ -> mVM.cancelCustomNoti(type) }
            .setOnCancelListener { mVM.cancelCustomNoti(type) }
            .create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
        editText.requestFocus()
    }
    private fun showAddWidgetDialog() {
        val context = requireContext()
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_widget, null)

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.add_widget)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, null)
            .create()

        val widgets = listOf(
            R.id.ll_add_resin_fixed to ResinWidget::class.java,
            R.id.ll_add_resin_resizable to ResinWidgetResizable::class.java,
            R.id.ll_add_mini to MiniWidget::class.java,
            R.id.ll_add_detail to DetailWidget::class.java,
            R.id.ll_add_talent to TalentWidget::class.java,
            R.id.ll_add_trailblaze_power to TrailPowerWidget::class.java,
            R.id.ll_add_hksr_detail to HKSRDetailWidget::class.java,
            R.id.ll_add_battery to BatteryWidget::class.java,
            R.id.ll_add_zzz_detail to ZZZDetailWidget::class.java
        )

        for ((viewId, widgetClass) in widgets) {
            dialogView.findViewById<View>(viewId).setOnClickListener {
                requestPinWidget(context, widgetClass)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun requestPinWidget(context: Context, widgetClass: Class<out AppWidgetProvider>) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val widgetProvider = ComponentName(context, widgetClass)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                appWidgetManager.requestPinAppWidget(widgetProvider, null, null)
            } else {
                makeToast(context, getString(R.string.msg_toast_widget_pin_not_supported))
            }
        } else {
            makeToast(context, getString(R.string.msg_toast_widget_pin_supports_android_8))
        }
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
                                makeToast(
                                    requireContext(),
                                    getString(R.string.data_save_mode_not_supported)
                                )
                            } else {
                                try {
                                    startActivity(Intent("android.settings.DATA_USAGE_SETTINGS"))
                                } catch (e: Exception) {
                                    log.e(e.message.toString())

                                    AlertDialog.Builder(requireActivity())
                                        .setMessage(R.string.msg_toast_data_save_mode)
                                        .setCancelable(false)
                                        .setPositiveButton("OK") { _dialog, _whichButton ->
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
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(Constant.HOW_CAN_I_GET_COOKIE_URL)
                            )
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

                                if (PreferenceManager.getString(
                                        it.baseContext,
                                        Constant.PREF_LOCALE,
                                        Locale.getDefault().language
                                    ) == locale
                                ) return@OnClickListener

                                PreferenceManager.setString(
                                    it.baseContext,
                                    Constant.PREF_LOCALE,
                                    locale
                                )

                                AlertDialog.Builder(requireActivity())
                                    .setTitle(R.string.dialog_restart)
                                    .setMessage(R.string.dialog_msg_restart)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.apply) { _dialog, whichButton ->
                                        log.e()
                                        CommonFunction.restartApp(it.baseContext)
                                    }
                                    .create()
                                    .show()
                            })
                    builder.show()
                }
            }

            is Event.ShowAddWidgetDialog -> showAddWidgetDialog()

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
                    if (event.isValid) CheckInWorker.startWorkerOneTimeImmediately(context, force = true)
                    else CheckInWorker.shutdownWorker(context)
                }
            }

            else -> {}
        }
    }
}