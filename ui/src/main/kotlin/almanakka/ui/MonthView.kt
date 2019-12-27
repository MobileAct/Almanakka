package almanakka.ui

import almanakka.core.DayOfWeek
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.Config
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import android.view.View.MeasureSpec.AT_MOST as atMostMeasureSpec
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec
import android.view.View.MeasureSpec.UNSPECIFIED as unspecifiedMeasureSpec
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT as wrapContent

@SuppressLint("ViewConstructor")
internal class MonthView(
        context: Context,
        private val viewProperty: ViewProperty,
        selectionProvider: ISelectionProvider,
        private val config: Config) : ViewGroup(context), IMonthContainerView {

    companion object {

        const val weekSize = 6

        // it value was provided MeasureSpec
        private const val maxHeight = 1073741823

        // FixMe: This define reason is only for Android Studio Bug.
        // I want define by import as, but it's recognized unused by Android Studio, so then auto delete when format import.
        private val focusDown = View.FOCUS_DOWN
        private val focusUp = View.FOCUS_UP
        private val focusLeft = View.FOCUS_LEFT
        private val focusRight = View.FOCUS_RIGHT
    }

    private val views = mutableListOf<View>()
    private val monthLabel: MonthLabelView
    private val weekLabel: WeekLabelView
    private val weeks: Array<WeekView>

    override val monthView: MonthView
        get() = this

    init {
        layoutParams = RecyclerView.LayoutParams(wrapContent, wrapContent)
        isFocusable = true

        monthLabel = MonthLabelView(context, config).apply {
            this@MonthView.addView(this)
        }.also {
            views.add(it)
        }

        weekLabel = WeekLabelView(context, config).apply {
            this@MonthView.addView(this)
        }.also {
            views.add(it)
        }

        weeks = Array(weekSize) {
            WeekView(context, selectionProvider, config).apply {
                this@MonthView.addView(this)
            }
        }.also {
            views.addAll(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = viewProperty.width
        var height = if (MeasureSpec.getMode(heightMeasureSpec) == unspecifiedMeasureSpec) maxHeight else viewProperty.height

        // padding
        height -= config.viewConfig.monthPaddingTop
        height -= config.viewConfig.monthPaddingBottom

        // measure MonthLabel
        monthLabel.measure(MeasureSpec.makeMeasureSpec(width, exactlyMeasureSpec), MeasureSpec.makeMeasureSpec(height, atMostMeasureSpec))
        height -= monthLabel.measuredHeight

        // measure WeekLabel
        weekLabel.measure(MeasureSpec.makeMeasureSpec(width, atMostMeasureSpec), MeasureSpec.makeMeasureSpec(height, atMostMeasureSpec))
        height -= weekLabel.measuredHeight

        // measure Weeks
        val heightPerElement = height / weekSize
        val remOfHeightPerElement = height % weekSize

        for (i in 0 until weekSize) {
            val elementHeight = heightPerElement + if (i < remOfHeightPerElement) 1 else 0
            weeks[i].measure(MeasureSpec.makeMeasureSpec(width, atMostMeasureSpec), MeasureSpec.makeMeasureSpec(elementHeight, atMostMeasureSpec))
        }

        // sum height
        var sumHeight = config.viewConfig.monthPaddingTop + config.viewConfig.monthPaddingBottom
        for (view in views) {
            sumHeight += view.measuredHeight
        }

        setMeasuredDimension(width, sumHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = config.viewConfig.monthPaddingTop

        for (view in views) {
            view.layout(0, top, view.measuredWidth, top + view.measuredHeight)

            top += view.measuredHeight
        }
    }

    fun setMonth(behaviorContainer: IBehaviorContainer, month: IMonth, dayOfWeekOrders: Array<DayOfWeek>) {
        check(month.weekSize <= 6) { "month must fewer than 7 weeks" }

        monthLabel.setMonth(behaviorContainer, month)

        weekLabel.setDayOfWeekOrders(behaviorContainer, dayOfWeekOrders)

        for ((i, week) in month.asSequence().withIndex()) {
            weeks[i].setWeek(behaviorContainer, month, week)
        }
        for (i in month.weekSize..weeks.lastIndex) {
            weeks[i].setWeek(behaviorContainer, month, null)
        }
    }

    fun findDayViewUnder(x: Float, y: Float): IDayView? {

        fun ViewGroup.findChildViewUnder(x: Float, y: Float): View? {
            val count = childCount
            for (i in count - 1 downTo 0) {
                val child = getChildAt(i)
                if (child.left <= x && x <= child.right
                        && child.top <= y && y <= child.bottom) {
                    return child
                }
            }
            return null
        }

        var parent: ViewGroup? = this
        // converting parent absolute position to child relative position
        var dx = x - left
        var dy = y - top

        while (parent != null) {
            val view = parent.findChildViewUnder(dx, dy)

            if (view is IDayView) {
                return view
            }

            parent = view as? ViewGroup
            // converting parent absolute position to child relative position
            dx -= parent?.left ?: 0
            dy -= parent?.top ?: 0
        }

        return null
    }

    override fun focusSearch(direction: Int): View? {
        when (direction) {
            focusDown, focusRight -> return weeks.first().getFirstDayView()
            focusUp, focusLeft -> return weeks.last { it.hasDay() }.getLastDayView()
        }
        return super.focusSearch(direction)
    }

    override fun focusSearch(focused: View?, direction: Int): View? {

        tailrec fun ViewParent.getParentMonth(): MonthView {
            if (this is MonthView) {
                return this
            }
            return parent.getParentMonth()
        }

        var nextFocusView = super.focusSearch(focused, direction)

        if (nextFocusView is IDayView) {
            val parentMonthOfFocusView = (nextFocusView as View).parent.getParentMonth()
            if (parentMonthOfFocusView != this) {
                nextFocusView = parentMonthOfFocusView
            }
        }

        // sometimes, find not focusable ScrollMonthContainerView when pager mode
        if (nextFocusView is ScrollMonthContainerView) {
            nextFocusView = nextFocusView.monthView
        }

        return nextFocusView
    }

    fun invalidateBackgroundView() {
        for (weekView in weeks) {
            weekView.invalidateBackgroundView()
        }
    }
}