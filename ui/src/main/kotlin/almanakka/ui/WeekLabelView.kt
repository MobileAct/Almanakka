package almanakka.ui

import almanakka.core.DayOfWeek
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.Config
import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.annotation.Size
import android.view.View.MeasureSpec.AT_MOST as atMostMeasureSpec
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec

@SuppressLint("ViewConstructor")
internal class WeekLabelView(context: Context, private val config: Config) : ViewGroup(context) {

    companion object {

        const val dayOfWeekSize = 7
    }

    private val dayOfWeeks: Array<DayLabelView>

    init {
        dayOfWeeks = Array(dayOfWeekSize) {
            DayLabelView(context, config.dayLabelConfig).apply {
                this@WeekLabelView.addView(this)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val contentWidth = width - config.viewConfig.monthPaddingSide * 2
        val widthPerElement = contentWidth / dayOfWeekSize
        val remOfWidthPerElement = contentWidth % dayOfWeekSize

        for (i in 0 until dayOfWeekSize) {
            val elementWidth = widthPerElement + if (i < remOfWidthPerElement) 1 else 0
            dayOfWeeks[i].measure(
                    MeasureSpec.makeMeasureSpec(elementWidth, exactlyMeasureSpec),
                    MeasureSpec.makeMeasureSpec(height, atMostMeasureSpec)
            )
        }

        var maxHeight = 0
        for (dayOfWeek in dayOfWeeks) {
            val measuredHeight = dayOfWeek.measuredHeight
            if (maxHeight < measuredHeight) {
                maxHeight = measuredHeight
            }
        }

        for (dayOfWeek in dayOfWeeks) {
            dayOfWeek.measure(
                    MeasureSpec.makeMeasureSpec(dayOfWeek.measuredWidth, exactlyMeasureSpec),
                    MeasureSpec.makeMeasureSpec(maxHeight, exactlyMeasureSpec)
            )
        }

        val contentHeight = maxHeight + config.dayLabelConfig.margin * 2
        setMeasuredDimension(width, contentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = config.viewConfig.monthPaddingSide
        val topOffset = config.dayLabelConfig.margin

        for (day in dayOfWeeks) {
            day.layout(left, topOffset, left + day.measuredWidth, topOffset + day.measuredHeight)
            left += day.measuredWidth
        }
    }

    fun setDayOfWeekOrders(behaviorContainer: IBehaviorContainer, @Size(7) dayOfWeeks: Array<DayOfWeek>) {
        check(dayOfWeeks.size == dayOfWeekSize) { "week must equal 7 dayOfWeeks" }

        for ((i, dayOfWeek) in dayOfWeeks.withIndex()) {
            this.dayOfWeeks[i].setDayOfWeek(behaviorContainer, dayOfWeek)
        }
    }
}