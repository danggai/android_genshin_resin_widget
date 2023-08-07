package danggai.app.presentation.ui.design

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
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
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.charaters.WidgetDesignCharacterFragment
import danggai.app.presentation.ui.design.charaters.select.WidgetDesignSelectCharacterFragment
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.ui.design.resin.WidgetDesignResinFragment
import danggai.app.presentation.ui.widget.*
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
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
        binding.vm = mVM
        binding.vm?.setCommonFun()

        context?.let { it ->
            storagePermissionCheck(it)
        }

        initTabLayout()

        initUi()
        initSf()
    }

    private fun initTabLayout() {
        val pagerAdapter = WidgetDesignAdapter(requireActivity()).apply {
            addFragment(WidgetDesignResinFragment())
            addFragment(WidgetDesignDetailFragment())
            addFragment(WidgetDesignCharacterFragment())
        }

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlTop, binding.vpMain) { tab, position ->
            tab.text = when (position) {
                0 -> "Stamina\u00A0"
                1 -> "Detail\u00A0"
                2 -> "Talent\u00A0"
                else -> ""
            }
        }.attach()
    }

    private fun initUi() {
        mVM.initUi()
        context?.let { it ->
            try {
                val wallpaperManager = WallpaperManager.getInstance(it)
                val wallpaperDrawable = wallpaperManager.drawable

                binding.vpMain.background = wallpaperDrawable
            } catch (e:SecurityException) {
                log.e()
                makeToast(it, getString(R.string.msg_toast_storage_permission_denied))
            }
        }
    }

    private fun storagePermissionCheck(context: Context) {
        if (PreferenceManager.getBoolean(context, Constant.PREF_CHECKED_STORAGE_PERMISSION, true)) {
            log.e()

            val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
                ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                PreferenceManager.setBoolean(context, Constant.PREF_CHECKED_STORAGE_PERMISSION, false)
                initUi()
            }

            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_permission)
                .setMessage(R.string.dialog_msg_permission_storage)
                .setCancelable(false)
                .setPositiveButton(R.string.apply) { dialog, whichButton ->
                    log.e()
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                .create()
                .show()
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfApplySavedData.collect {
                    context?.let { _context ->
                        log.e()
                        CommonFunction.sendBroadcastAppWidgetUpdate<ResinWidget>(_context)
                        CommonFunction.sendBroadcastAppWidgetUpdate<ResinWidgetResizable>(_context)
                        CommonFunction.sendBroadcastAppWidgetUpdate<DetailWidget>(_context)
                        CommonFunction.sendBroadcastAppWidgetUpdate<MiniWidget>(_context)
                        CommonFunction.sendBroadcastAppWidgetUpdate<TrailPowerWidget>(_context)
                        CommonFunction.sendBroadcastAppWidgetUpdate<HKSRDetailWidget>(_context)
                        _context.sendBroadcast(
                            Intent(_context, TalentWidget::class.java)
                                .setAction(Constant.ACTION_TALENT_WIDGET_REFRESH)
                        )
                    }
                }
            }

            launch {
                mVM.sfStartSelectFragment.collect {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down)
                        .add(R.id.fragment,
                            WidgetDesignSelectCharacterFragment.newInstance(),
                            WidgetDesignSelectCharacterFragment.TAG)
                        .commit()
                }
            }
        }
    }
}