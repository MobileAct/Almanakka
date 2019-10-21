package almanakka.ui.providers

import almanakka.core.IDay
import almanakka.core.ImmutableDay
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.core.behaviors.ISelectableBehavior
import almanakka.ui.*
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.events.EventArgs
import almanakka.ui.events.RangeDaySelectedEventArgs
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.AutoScrollHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent.ACTION_DOWN as actionDown
import android.view.MotionEvent.ACTION_UP as actionUp


class SlideRangeSelectionProvider(
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

    private val autoScrollHelper = RecyclerAutoScrollHelper(recyclerView).apply {
        isEnabled = true
    }

    private var isRangeSelecting = false
    private var previousTouchedDayView: IDayView? = null

    private val adapter: MonthAdapter?
        get() = recyclerView.adapter as? MonthAdapter

    private var behaviorContainer: IBehaviorContainer? = null
    private val selectableBehavior = SelectableBehavior()

    override var viewState: ViewState = ViewState()

    /**
     * [selectedMinDay] or [selectedMaxDay]
     */
    private var selectedBaseEdgeDay: IDay? = null

    override fun registerBehavior(behaviorContainer: IBehaviorContainer) {
        this.behaviorContainer = behaviorContainer
        behaviorContainer.register(selectableBehavior)
    }

    private fun requireBehaviorContainer(): IBehaviorContainer {
        return requireNotNull(behaviorContainer)
    }

    override fun select(day: IDay) {
        selectableBehavior.clearSelect()

        previousTouchedDayView = null

        selectableBehavior.select(day)

        recyclerView.adapter?.notifyDataSetChanged()
        raiseSelectEvent()
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
        handleTouchEvent(rv, e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return handleTouchEvent(rv, e)
    }

    private fun handleTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == actionUp && isRangeSelecting) {
            showUnPressBackgroundAnimation()

            autoScrollHelper.onTouch(rv, e)
            isRangeSelecting = false
        }

        val monthView = rv.findChildViewUnder(e.x, e.y) as? MonthView
        val dayView = monthView?.findDayViewUnder(e.x, e.y)

        // touch moving or up when selecting
        if (isRangeSelecting) {
            autoScrollHelper.onTouch(rv, e)

            if (dayView != null) {
                handleDayViewTouch(rv, dayView, false)
            }
            return true
        }

        // touch down when selected
        if (e.action == actionDown && dayView?.getDay()?.isSelectedEdge() == true) {
            showPressBackgroundAnimation()

            autoScrollHelper.onTouch(rv, e)

            handleDayViewTouch(rv, dayView, true)
            isRangeSelecting = true
            return true
        }

        return false
    }

    private fun handleDayViewTouch(rv: RecyclerView, dayView: IDayView, isTouchDown: Boolean) {
        if (previousTouchedDayView == dayView) {
            return
        }

        val day = dayView.getDay() ?: return
        val previousSelectedMinDay = selectableBehavior.selectedMinDay
        val previousSelectedMaxDay = selectableBehavior.selectedMaxDay

        val selectedMinDay = selectableBehavior.selectedMinDay
        val selectedMaxDay = selectableBehavior.selectedMaxDay

        if (isTouchDown) {
            selectedBaseEdgeDay = when {
                selectedMaxDay == null -> selectedMinDay
                selectedMinDay != null && day <= selectedMinDay -> selectedMaxDay
                else -> selectedMinDay
            }
        }

        val selectedBaseEdgeDay = selectedBaseEdgeDay
        if (selectedBaseEdgeDay != null) {
            selectableBehavior.clearSelect()
            selectableBehavior.selectBaseDay(selectedBaseEdgeDay)
            selectableBehavior.select(day)
        }

        rv.adapter?.notifyDataSetChanged()
        previousTouchedDayView = dayView

        if (previousSelectedMinDay != selectableBehavior.selectedMinDay || previousSelectedMaxDay != selectableBehavior.selectedMaxDay) {
            raiseSelectEvent()
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    private fun showPressBackgroundAnimation() {
        viewState.isSelectedBackgroundPressed = true
        viewState.selectedTranslationZ = viewConfig.selectedElevation
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showUnPressBackgroundAnimation() {
        viewState.isSelectedBackgroundPressed = false
        viewState.selectedTranslationZ = 0F
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun IDay.isSelectedEdge(): Boolean {
        val previousDay = previousDay
        val nextDay = nextDay

        val isSelected = selectableBehavior.isSelected(this)
        val isSelectedPrevious = if (previousDay != null) selectableBehavior.isSelected(previousDay) else false
        val isSelectedNext = if (nextDay != null) selectableBehavior.isSelected(nextDay) else false

        if (isSelected.not()) {
            return false
        }

        if (isSelectedNext.not() && isSelectedPrevious) {
            return true
        }
        if (isSelectedNext && isSelectedPrevious.not()) {
            return true
        }
        if (isSelected && isSelectedNext.not() && isSelectedPrevious.not()) {
            return true
        }

        return false
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

    class RecyclerAutoScrollHelper(target: RecyclerView) : AutoScrollHelper(target) {

        private val recyclerView = target

        override fun canTargetScrollVertically(direction: Int): Boolean {
            return true
        }

        override fun canTargetScrollHorizontally(direction: Int): Boolean {
            return false
        }

        override fun scrollTargetBy(deltaX: Int, deltaY: Int) {
            recyclerView.scrollBy(deltaX, deltaY)
        }
    }

    private inner class SelectableBehavior : ISelectableBehavior {

        var selectedMinDay: IDay? = null
            private set
        var selectedMaxDay: IDay? = null
            private set

        fun selectBaseDay(baseDay: IDay) {
            selectedMinDay = baseDay
            selectedMaxDay = null
        }

        fun select(day: IDay) {
            val selectedMinDay = selectedMinDay
            if (selectedMinDay == null) {
                this.selectedMinDay = day
                return
            }

            val selectingDay = daySequence(selectedMinDay, day)
                    .takeWhile { requireBehaviorContainer().isDisable(it).not() }
                    .last()

            this.selectedMinDay = min(selectedMinDay, selectingDay)
            this.selectedMaxDay = max(selectedMinDay, selectingDay)
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