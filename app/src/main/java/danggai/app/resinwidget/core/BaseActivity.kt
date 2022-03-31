package danggai.app.resinwidget.core

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseActivity: AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    fun Disposable.addDisposableExt() {
        compositeDisposable.add(this)
    }
}