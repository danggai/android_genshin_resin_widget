package danggai.app.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import danggai.app.presentation.R

class DetailWidgetRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val iconImageView: ImageView
    private val titleTextView: TextView
    private val valueTextView: TextView

    companion object {
        const val DEFAULT_DRAWABLE_ID = 0
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_detail_widget_row, this, true)

        iconImageView = findViewById(R.id.iv_icon)
        titleTextView = findViewById(R.id.tv_title)
        valueTextView = findViewById(R.id.tv_value)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DetailWidgetRow, 0, 0).apply {
            try {
                // titleText 속성 읽기
                val title = getString(R.styleable.DetailWidgetRow_rowTitleText) ?: "Title"
                setTitle(title)

                // valueText 속성 읽기
                val value = getString(R.styleable.DetailWidgetRow_rowValueText) ?: "Value"
                setValue(value)

                // iconDrawable 속성 읽기
                val iconResId =
                    getResourceId(R.styleable.DetailWidgetRow_iconDrawable, DEFAULT_DRAWABLE_ID)
                setIcon(iconResId)
            } finally {
                recycle()
            }
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setValue(value: String) {
        valueTextView.text = value
    }

    private fun setIcon(iconResId: Int) {
        if (iconResId != DEFAULT_DRAWABLE_ID)
            iconImageView.setImageResource(iconResId)
        else iconImageView.visibility = INVISIBLE
    }
}
