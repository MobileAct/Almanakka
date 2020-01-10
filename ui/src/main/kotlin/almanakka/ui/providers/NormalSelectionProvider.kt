package almanakka.ui.providers

import almanakka.core.IDay
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.core.behaviors.ISelectableBehavior
import almanakka.ui.*
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NormalSelectionProvider(
        private val calendarView: CalendarView,
        private val recyclerView: RecyclerView,
        private val viewConfig: ViewConfig,
        private val daySelected: (EventArgs) -> Unit) : ISelectionProvider {

    companion object {

        private const val savedStateSelectedNull = "saved_state_selected_null"
        private const val savedStateSelectedYear = "saved_state_selected_year"
        private const val savedStateSelectedMonth = "saved_state_selected_month"
        private const val savedStateSelectedDay = "saved_state_selected_day"
    }

    private val adapter: MonthAdapter?
        get() = recyclerView.adapter as? MonthAdapter
    private var behaviorContainer: IBehaviorContainer? = null
    private val selectableBehavior = SelectableBehavior()

    override fun createBackgroundView(context: Context): BackgroundView {
        return NormalBackgroundView(context, viewConfig)
    }

    override fun requestPostInvalidateView(): Boolean {
        return false
    }

    override fun registerBehavior(behaviorContainer: IBehaviorContainer) {
        this.behaviorContainer = behaviorContainer
        behaviorContainer.register(selectableBehavior)
    }

    private fun requireBehaviorContainer(): IBehaviorContainer {
        return requireNotNull(behaviorContainer)
    }

    override fun select(day: IDay) {
        selectableBehavior.select(day)

        calendarView.invalidate()
        daySelected(DaySelectedEventArgs(day.toImmutable()))
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

    override fun createState(): Bundle {
        val state = Bundle()
        val day = this.selectableBehavior.selectedDay

        if (day == null) {
            state.putBoolean(savedStateSelectedNull, true)
            return state
        } else {
            state.putBoolean(savedStateSelectedNull, false)
        }

        state.putShort(savedStateSelectedYear, day.year)
        state.putByte(savedStateSelectedMonth, day.month)
        state.putByte(savedStateSelectedDay, day.day)

        return state
    }

    override fun restoreState(state: Bundle) {
        if (state.getBoolean(savedStateSelectedNull)) {
            return
        }

        val year = state.getShort(savedStateSelectedYear)
        val month = state.getByte(savedStateSelectedMonth)
        val day = state.getByte(savedStateSelectedDay)

        val selectedDay = adapter?.calendar?.findDay(year, month, day) ?: return
        select(selectedDay)
    }

    private inner class SelectableBehavior : ISelectableBehavior {

        var selectedDay: IDay? = null
            private set

        fun select(day: IDay) {
            this.selectedDay = day
        }

        override fun isSelected(day: IDay): Boolean {
            return day == this.selectedDay
        }
    }
}