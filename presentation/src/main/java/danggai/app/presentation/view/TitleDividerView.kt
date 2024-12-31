package danggai.app.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import danggai.app.presentation.R

class TitleDividerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var titleTextView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_title_divider, this, true)

        titleTextView = findViewById(R.id.tv_title)

        context.theme.obtainStyledAttributes(attrs, R.styleable.TitleDividerView, 0, 0).apply {
            try {
                val title = getString(R.styleable.TitleDividerView_titleText) ?: "Default Title"
                setTitle(title)

                val marginTop =
                    getDimension(R.styleable.TitleDividerView_marginTop, DEFALUT_MARGIN_TOP)

                if (marginTop != DEFALUT_MARGIN_TOP) {
                    setMarignTop(marginTop)
                }
            } finally {
                recycle()
            }
        }
    }

    private fun setTitle(title: String) {
        titleTextView.text = title
    }

    private fun setMarignTop(marginTop: Float) {
        layoutParams = (layoutParams as? MarginLayoutParams)?.apply {
            topMargin = marginTop.toInt()
        }
    }

    companion object {
        const val DEFALUT_MARGIN_TOP = -1f
    }
}
