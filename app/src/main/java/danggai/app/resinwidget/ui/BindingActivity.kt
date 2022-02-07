package danggai.app.resinwidget.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import danggai.app.resinwidget.ui.base.BaseActivity
import danggai.app.resinwidget.util.LocaleWrapper

abstract class BindingActivity<T: ViewDataBinding> : BaseActivity() {
    @LayoutRes
    abstract fun getLayoutResId(): Int

    protected lateinit var binding: T
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutResId())
    }

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleWrapper.wrap(baseContext))
    }
}