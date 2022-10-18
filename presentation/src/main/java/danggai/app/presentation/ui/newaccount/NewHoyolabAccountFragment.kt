package danggai.app.presentation.ui.newaccount

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentNewHoyolabAccountBinding
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.cookie.CookieWebViewActivity
import danggai.app.presentation.ui.design.WidgetDesignActivity
import danggai.app.presentation.ui.design.charaters.WidgetDesignCharacterFragment
import danggai.app.presentation.ui.design.charaters.select.WidgetDesignSelectCharacterFragment
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.ui.design.resin.WidgetDesignResinFragment
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.CheckInWorker
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.util.Constant
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class NewHoyolabAccountFragment : BindingFragment<FragmentNewHoyolabAccountBinding, NewHoyolabAccountViewModel>() {

    companion object {
        val TAG: String = NewHoyolabAccountFragment::class.java.simpleName
        fun newInstance() = NewHoyolabAccountFragment()
    }

    private val mVM: NewHoyolabAccountViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_new_hoyolab_account

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        initSf()
    }

    fun onNewIntent(intent: Intent?) {
        intent?.let {
            try {
                log.e()
                val cookie = it.getStringExtra(NewHoyolabAccountActivity.ARG_PARAM_COOKIE)!!
                mVM.sfHoyolabCookie.value = cookie
            } catch (e: Exception) {
                log.e(e.toString())
            }
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
//            launch {
//                mVM.sfApplySavedData.collect {
//                    context?.let { _context ->
//                        log.e()
//                        _context.sendBroadcast(
//                            Intent(_context, TalentWidget::class.java)
//                                .setAction(Constant.ACTION_TALENT_WIDGET_REFRESH)
//                        )
//                    }
//                }
//            }
//
//            launch {
//                mVM.sfStartSelectFragment.collect {
//                    parentFragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down)
//                        .add(R.id.fragment,
//                            WidgetDesignSelectCharacterFragment.newInstance(),
//                            WidgetDesignSelectCharacterFragment.TAG)
//                        .commit()
//                }
//            }
        }
    }


    override fun handleEvents(event: Event) {
        super.handleEvents(event)

        when (event) {
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
        }
    }
}