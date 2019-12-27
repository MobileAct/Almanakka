package almanakka.ui

import almanakka.core.IMonth
import almanakka.core.IWeek
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.Config
import almanakka.ui.internal.ViewMeasure
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.View.MeasureSpec.AT_MOST as atMostMeasureSpec
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec

@SuppressLint("ViewConstructor")
internal class WeekView(
        context: Context,
        selectionProvider: ISelectionProvider,
        private val config: Config) : ViewGroup(context) {

    companion object {

        const val daySize = 7
    }

    private val backgroundView: BackgroundView
    private val days: Array<IDayView>
    private var currentWeek: IWeek? = null

    init {
        days = Array(daySize) {
            DayView(context, selectionProvider, config.dayConfig)
        }

        backgroundView = selectionProvider.createBackgroundView(context).apply {
            this@WeekView.addView(this)
        }

        for (day in days) {
            addView(day as DayView)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        if (currentWeek == null) {
            setMeasuredDimension(width, 0)
            return
        }

        val elementWidths = ViewMeasure.measureDayWidth(config.viewConfig, width)
        for (i in 0 until daySize) {
            val elementWidth = elementWidths[i] // array may be equals count
            days[i].measure(
                    MeasureSpec.makeMeasureSpec(elementWidth, exactlyMeasureSpec),
                    MeasureSpec.makeMeasureSpec(height, atMostMeasureSpec)
            )
        }

        var maxHeight = 0
        for (day in days) {
            val measuredHeight = day.getMeasuredHeight()
            if (maxHeight < measuredHeight) {
                maxHeight = measuredHeight
            }
        }

        for (day in days) {
            day.measure(
                    MeasureSpec.makeMeasureSpec(day.getMeasuredWidth(), exactlyMeasureSpec),
                    MeasureSpec.makeMeasureSpec(maxHeight, exactlyMeasureSpec)
            )
        }

        backgroundView.measure(
                MeasureSpec.makeMeasureSpec(width, atMostMeasureSpec),
                MeasureSpec.makeMeasureSpec(maxHeight, exactlyMeasureSpec)
        )

        val contentHeight = maxHeight + config.dayConfig.margin * 2
        setMeasuredDimension(width, contentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = config.viewConfig.monthPaddingSide
        val topOffset = config.dayConfig.margin

        backgroundView.layout(
                0,
                topOffset,
                backgroundView.measuredWidth,
                topOffset + backgroundView.measuredHeight
        )

        for (day in days) {
            day.layout(left, topOffset, left + day.getMeasuredWidth(), topOffset + day.getMeasuredHeight())
            left += day.getMeasuredWidth()
        }
    }

    fun setWeek(behaviorContainer: IBehaviorContainer, month: IMonth, week: IWeek?) {
        currentWeek = week

        if (week == null) {
            for (day in days) {
                day.setDay(behaviorContainer, month, null)
            }
            return
        }

        check(week.asSequence().any()) { "week must have day" }
        check(week.daySize == daySize) { "week must have 7 days" }

        for ((i, day) in week.asSequence().withIndex()) {
            days[i].setDay(behaviorContainer, month, day)
        }

        backgroundView.updateState(behaviorContainer, week)
    }

    fun getFirstDayView(): View? {
        return days.firstOrNull { it.getDay() != null } as? View
    }

    fun getLastDayView(): View? {
        return days.lastOrNull { it.getDay() != null } as? View
    }

    fun hasDay(): Boolean {
        return days.any { it.getDay() != null }
    }

    fun invalidateBackgroundView() {
        backgroundView.postInvalidateOnAnimation()
        for (day in days) {
            day.postInvalidateOnAnimation()
        }
    }
}