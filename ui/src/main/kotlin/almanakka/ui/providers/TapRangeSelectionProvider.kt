package almanakka.ui.providers

import almanakka.core.IDay
import almanakka.core.ImmutableDay
import almanakka.core.animators.Animator
import almanakka.core.animators.IAnimator
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.core.behaviors.ISelectableBehavior
import almanakka.ui.*
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.events.EventArgs
import almanakka.ui.events.RangeDaySelectedEventArgs
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TapRangeSelectionProvider(
        private val calendarView: CalendarView,
        private val recyclerView: RecyclerView,
        private val viewConfig: ViewConfig,
        private val daySelected: (EventArgs) -> Unit) : ISelectionProvider {

    companion object {

        private const val savedStateSelectedMinNull = "saved_state_selected_min_null"
        private const val savedStateSelectedMinYear = "saved_state_selected_min_year"
        private const val savedStateSelectedMinMonth = "saved_state_selected_min_month"
        private const val savedStateSelectedMinDay = "saved_state_selected_min_day"
        private const val savedStateSelectedMaxNull = "saved_state_selected_max_null"
        private const val savedStateSelectedMaxYear = "saved_state_selected_max_year"
        private const val savedStateSelectedMaxMonth = "saved_state_selected_max_month"
        private const val savedStateSelectedMaxDay = "saved_state_selected_max_day"
    }

    private val adapter: MonthAdapter?
        get() = recyclerView.adapter as? MonthAdapter

    var animator: IAnimator? = null

    private var behaviorContainer: IBehaviorContainer? = null
    private val selectableBehavior = SelectableBehavior()

    override fun registerBehavior(behaviorContainer: IBehaviorContainer) {
        this.behaviorContainer = behaviorContainer
        behaviorContainer.register(selectableBehavior)
    }

    private fun requireBehaviorContainer(): IBehaviorContainer {
        return requireNotNull(behaviorContainer)
    }

    override fun select(day: IDay) {
        if (selectableBehavior.selectedMinDay != null && selectableBehavior.selectedMaxDay != null) {
            // state of reset
            selectableBehavior.clearSelect()
            selectableBehavior.select(day)
        } else {
            // state of initial display
            // state of selecting edge end

            selectableBehavior.select(day)
        }

        calendarView.invalidate()
        raiseSelectEvent()
    }

    override fun createBackgroundView(context: Context): BackgroundView {
        return TapRangeBackgroundView(context, this, viewConfig)
    }

    override fun requestPostInvalidateView(): Boolean {
        return animator?.isAnimating() == true
    }

    override fun onClick(v: View?) {
        val dayView = v as? IDayView
        val day = dayView?.getDay() ?: return

        if (requireBehaviorContainer().isDisable(day)) {
            return
        }

        select(day)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    private fun raiseSelectEvent() {
        val min = selectableBehavior.selectedMinDay ?: return
        val selected = mutableListOf<ImmutableDay>()

        var current: IDay? = min
        while (current != null) {
            if (selectableBehavior.isSelected(current).not()) {
                break
            }
            selected.add(current.toImmutable())
            current = current.nextDay
        }
        daySelected(RangeDaySelectedEventArgs(selected.toTypedArray()))
    }

    override fun createState(): Bundle {
        val state = Bundle()
        storeSelectedMinDayState(state)
        storeSelectedMaxDayState(state)
        return state
    }

    private fun storeSelectedMinDayState(state: Bundle) {
        val day = selectableBehavior.selectedMinDay
        if (day == null) {
            state.putBoolean(savedStateSelectedMinNull, true)
            return
        } else {
            state.putBoolean(savedStateSelectedMinNull, false)
        }

        state.putShort(savedStateSelectedMinYear, day.year)
        state.putByte(savedStateSelectedMinMonth, day.month)
        state.putByte(savedStateSelectedMinDay, day.day)
    }

    private fun storeSelectedMaxDayState(state: Bundle) {
        val day = selectableBehavior.selectedMaxDay
        if (day == null) {
            state.putBoolean(savedStateSelectedMaxNull, true)
            return
        } else {
            state.putBoolean(savedStateSelectedMaxNull, false)
        }

        state.putShort(savedStateSelectedMaxYear, day.year)
        state.putByte(savedStateSelectedMaxMonth, day.month)
        state.putByte(savedStateSelectedMaxDay, day.day)
    }

    override fun restoreState(state: Bundle) {
        restoreSelectedMinDay(state)
        restoreSelectedMaxDay(state)
    }

    private fun restoreSelectedMinDay(state: Bundle) {
        if (state.getBoolean(savedStateSelectedMinNull)) {
            return
        }

        val year = state.getShort(savedStateSelectedMinYear)
        val month = state.getByte(savedStateSelectedMinMonth)
        val day = state.getByte(savedStateSelectedMinDay)

        val selectedMinDay = adapter?.calendar?.findDay(year, month, day) ?: return
        selectableBehavior.select(selectedMinDay)
    }

    private fun restoreSelectedMaxDay(state: Bundle) {
        if (state.getBoolean(savedStateSelectedMaxNull)) {
            return
        }

        val year = state.getShort(savedStateSelectedMaxYear)
        val month = state.getByte(savedStateSelectedMaxMonth)
        val day = state.getByte(savedStateSelectedMaxDay)

        val selectedMaxDay = adapter?.calendar?.findDay(year, month, day) ?: return
        selectableBehavior.select(selectedMaxDay)
    }

    private inner class SelectableBehavior : ISelectableBehavior {

        var selectedMinDay: IDay? = null
            private set
        var selectedMaxDay: IDay? = null
            private set

        fun select(day: IDay) {
            val selectedMinDay = selectedMinDay
            if (selectedMinDay == null) {
                animator = animator ?: Animator(100, day)// ToDo: from config
                this.selectedMinDay = day
                return
            }

            val selectingDay = daySequence(selectedMinDay, day)
                    .takeWhile { requireBehaviorContainer().isDisable(it).not() }
                    .last()

            this.selectedMinDay = min(selectedMinDay, selectingDay)
            this.selectedMaxDay = max(selectedMinDay, selectingDay)
            animator?.updateMeasureTime()
            animator?.select(min(selectedMinDay, selectingDay), max(selectedMinDay, selectingDay))
            animator = animator ?: Animator(100, day) // ToDo: from config
        }

        private fun daySequence(start: IDay, end: IDay): Sequence<IDay> {
            if (start < end) {
                return generateSequence(start) { if (it < end) it.nextDay else null }
            } else {
                return generateSequence(start) { if (end < it) it.previousDay else null }
            }
        }

        fun clearSelect() {
            this.selectedMinDay = null
            this.selectedMaxDay = null
            animator = null
        }

        private fun min(day1: IDay, day2: IDay): IDay {
            return if (day1 < day2) day1 else day2
        }

        private fun max(day1: IDay, day2: IDay): IDay {
            return if (day1 < day2) day2 else day1
        }

        override fun isSelected(day: IDay): Boolean {
            val selectedMinDay = selectedMinDay
            val selectedMaxDay = selectedMaxDay

            if (selectedMinDay != null && selectedMaxDay != null) {
                return selectedMinDay <= day && day <= selectedMaxDay
            } else if (selectedMinDay != null) {
                return day == selectedMinDay
            } else {
                return false
            }
        }
    }
}