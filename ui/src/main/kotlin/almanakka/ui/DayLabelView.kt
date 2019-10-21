package almanakka.ui

import almanakka.core.DayOfWeek
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.DayLabelConfig
import almanakka.ui.extensions.setDayLabelTextStyleAndPrecomputedTextFuture
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("ViewConstructor")
internal class DayLabelView(context: Context, private val dayLabelConfig: DayLabelConfig) : FrameLayout(context) {

    private val text: AppCompatTextView

    init {
        text = AppCompatTextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            setPadding(dayLabelConfig.padding, dayLabelConfig.padding, dayLabelConfig.padding, dayLabelConfig.padding)

            this@DayLabelView.addView(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        text.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST))

        setMeasuredDimension(width, text.measuredHeight)
    }

    fun setDayOfWeek(behaviorContainer: IBehaviorContainer, dayOfWeek: DayOfWeek) {
        behaviorContainer.setDayLabelTextStyleAndPrecomputedTextFuture(dayOfWeek, text)
    }
}