package almanakka.ui

import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.Config
import almanakka.ui.extensions.setMonthLabelTextStyleAndText
import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import android.view.Gravity.CENTER as centerGravity
import android.view.View.MeasureSpec.AT_MOST as atMostMeasureSpec
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec

@SuppressLint("ViewConstructor")
class MonthLabelView(context: Context, private val config: Config)
    : ViewGroup(context), IMonthLabelView {

    private val monthLabel: AppCompatTextView

    init {
        monthLabel = AppCompatTextView(context).apply {
            val padding = config.monthLabelConfig.padding
            setPadding(padding, padding, padding, padding)

            gravity = centerGravity
            this@MonthLabelView.addView(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val contentWidth = width - config.viewConfig.monthPaddingSide * 2

        monthLabel.measure(
                MeasureSpec.makeMeasureSpec(contentWidth, exactlyMeasureSpec),
                MeasureSpec.makeMeasureSpec(height, atMostMeasureSpec)
        )

        val contentHeight = monthLabel.measuredHeight + config.monthLabelConfig.margin * 2

        setMeasuredDimension(width, contentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        monthLabel.layout(
                config.viewConfig.monthPaddingSide,
                config.monthLabelConfig.margin,
                config.viewConfig.monthPaddingSide + monthLabel.measuredWidth,
                config.monthLabelConfig.margin + monthLabel.measuredHeight
        )
    }

    override fun setMonth(behaviorContainer: IBehaviorContainer, month: IMonth) {
        behaviorContainer.setMonthLabelTextStyleAndText(month, monthLabel)
    }
}