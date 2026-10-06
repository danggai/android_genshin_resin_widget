package danggai.app.presentation.view

import android.content.Context
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import danggai.app.presentation.R

class TitleDividerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var titleTextView: TextView
    private var arrowImageView: ImageView
    private var isFirstExpanded = true

    init {
        LayoutInflater.from(context).inflate(R.layout.view_title_divider, this, true)

        titleTextView = findViewById(R.id.tv_title)
        arrowImageView = findViewById(R.id.iv_arrow)

        context.theme.obtainStyledAttributes(attrs, R.styleable.TitleDividerView, 0, 0).apply {
            try {
                val title = getString(R.styleable.TitleDividerView_titleText) ?: "Default Title"
                setTitle(title)

                val marginTop =
                    getDimension(R.styleable.TitleDividerView_marginTop, DEFALUT_MARGIN_TOP)

                if (marginTop != DEFALUT_MARGIN_TOP) {
                    setMarignTop(marginTop)
                }

                if (getBoolean(R.styleable.TitleDividerView_collapsible, false)) {
                    arrowImageView.visibility = VISIBLE
                }

                if (hasValue(R.styleable.TitleDividerView_titleColor)) {
                    val color = getColor(R.styleable.TitleDividerView_titleColor, 0)
                    titleTextView.setTextColor(color)
                    arrowImageView.imageTintList = ColorStateList.valueOf(color)
                }

                if (!getBoolean(R.styleable.TitleDividerView_dividerVisible, true)) {
                    findViewById<View>(R.id.v_divider).visibility = INVISIBLE
                }

                if (!getBoolean(R.styleable.TitleDividerView_titleItalic, true)) {
                    titleTextView.setTypeface(titleTextView.typeface, Typeface.BOLD)
                }

                getResourceId(R.styleable.TitleDividerView_titleIcon, 0).let { iconId ->
                    if (iconId != 0) {
                        val size = (24 * resources.displayMetrics.density).toInt()
                        val icon = ContextCompat.getDrawable(context, iconId)
                        icon?.setBounds(0, 0, size, size)
                        titleTextView.setCompoundDrawables(icon, null, null, null)
                        titleTextView.compoundDrawablePadding = (8 * resources.displayMetrics.density).toInt()
                    }
                }            } finally {
                recycle()
            }
        }
    }

    fun setExpanded(expanded: Boolean) {
        val rotation = if (expanded) 180f else 0f
        if (isFirstExpanded) {
            arrowImageView.rotation = rotation
            isFirstExpanded = false
        } else {
            arrowImageView.animate().rotation(rotation).setDuration(200L).start()
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
