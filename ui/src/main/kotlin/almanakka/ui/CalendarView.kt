package almanakka.ui

import almanakka.core.CalendarCollection
import almanakka.core.Day
import almanakka.core.DayOfWeek
import almanakka.core.IDay
import almanakka.core.extensions.toDay
import almanakka.ui.behaviors.DayLabelTextBehavior
import almanakka.ui.behaviors.DayTextBehavior
import almanakka.ui.behaviors.MonthLabelTextBehavior
import almanakka.ui.configurations.*
import almanakka.ui.events.EventArgs
import almanakka.ui.events.EventHandler
import almanakka.ui.providers.NormalSelectionProvider
import almanakka.ui.providers.SlideRangeSelectionProvider
import almanakka.ui.providers.TapRangeSelectionProvider
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec
import android.view.ViewGroup.LayoutParams.MATCH_PARENT as matchParent
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL as linearLayoutManagerOrientationHorizontal
import java.util.Calendar as JCalendar
import java.util.Locale.US as localeUs

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1)
    : FrameLayout(context, attrs, defStyleAttr) {

    companion object {

        private const val savedStateCalendarView = "saved_state_calendar_view"
        private const val savedStateLayoutManager = "saved_state_layout_manager"
        private const val savedStateSelectionProvider = "saved_state_selection_provider"

        private const val normalProvider = 0
        private const val tapRangeProvider = 1
        private const val slideRangeProvider = 2

        private const val scrollMode = 0
        private const val pagerMode = 1

        private val dateFormat = SimpleDateFormat("yyyy MM/dd", localeUs)
    }

    private val dayConfig = DayConfig(context, attrs, defStyleAttr)
    private val dayTextBehavior = DayTextBehavior(dayConfig)

    private val dayLabelConfig = DayLabelConfig(context, attrs, defStyleAttr)
    private val dayLabelTextBehavior = DayLabelTextBehavior(dayLabelConfig)

    private val monthLabelConfig = MonthLabelConfig(context, attrs, defStyleAttr)
    private val monthLabelTextBehavior = MonthLabelTextBehavior(monthLabelConfig)

    private val viewConfig = ViewConfig(context, attrs, defStyleAttr)

    private val isStickyHeader: Boolean
    private val stickyHeaderBackgroundColor: Int
    private val selectionProviderType: Int
    private val minDay: Day
    private val maxDay: Day
    private val isShowDaysOfDifferentMonth: Boolean
    private val dayOfWeekOrderStart: Int
    private val mode: Int

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        isStickyHeader = typedArray.getBoolean(R.styleable.CalendarView_isStickyHeader, false)
        val defaultStickyBackgroundColor = ContextCompat.getColor(context, R.color.background)
        stickyHeaderBackgroundColor = typedArray.getColor(R.styleable.CalendarView_stickyHeaderBackgroundColor, defaultStickyBackgroundColor)

        val minDayText = typedArray.getString(R.styleable.CalendarView_minDay)
        if (minDayText != null) {
            val date = dateFormat.parse(minDayText)
            val calendar = JCalendar.getInstance().apply {
                time = date
            }
            minDay = calendar.toDay()
        } else {
            minDay = JCalendar.getInstance().toDay()
        }

        val maxDayText = typedArray.getString(R.styleable.CalendarView_maxDay)
        if (maxDayText != null) {
            val date = dateFormat.parse(maxDayText)
            val calendar = JCalendar.getInstance().apply {
                time = date
            }
            maxDay = calendar.toDay()
        } else {
            maxDay = JCalendar.getInstance().toDay()
        }

        isShowDaysOfDifferentMonth = typedArray.getBoolean(R.styleable.CalendarView_isShowDaysOfDifferentMonth, false)
        dayOfWeekOrderStart = typedArray.getInt(R.styleable.CalendarView_dayOfWeekOrderStart, DayOfWeek.firstday.value)

        selectionProviderType = typedArray.getInt(R.styleable.CalendarView_selectionProvider, normalProvider)

        mode = typedArray.getInt(R.styleable.CalendarView_mode, scrollMode)

        typedArray.recycle()
    }

    private val viewProperty = ViewProperty()
    private val recyclerView: RecyclerView
    private val monthAdapter: MonthAdapter
        get() = recyclerView.adapter as MonthAdapter
    private var selectionProvider: ISelectionProvider

    val controller: CalendarController
    val daySelected = EventHandler<CalendarView, EventArgs>()

    init {
        val calendarCollection = CalendarCollection.create(
                minDay = this@CalendarView.minDay,
                maxDay = this@CalendarView.maxDay,
                dayOfWeekOrderStart = DayOfWeek.from(this@CalendarView.dayOfWeekOrderStart),
                isShowDaysOfDifferentMonth = this@CalendarView.isShowDaysOfDifferentMonth
        ).apply {
            behaviorContainer.register(dayTextBehavior)
            behaviorContainer.register(dayLabelTextBehavior)
            behaviorContainer.register(monthLabelTextBehavior)
        }

        recyclerView = RecyclerView(context).apply {
            setHasFixedSize(true)
            setItemViewCacheSize(4) // increase size from default in standard

            selectionProvider = when (this@CalendarView.selectionProviderType) {
                tapRangeProvider -> TapRangeSelectionProvider(this@CalendarView, this, viewConfig, ::raiseDaySelected)
                slideRangeProvider -> SlideRangeSelectionProvider(this@CalendarView, this, viewConfig, ::raiseDaySelected)
                else -> NormalSelectionProvider(this@CalendarView, this, ::raiseDaySelected)
            }
            selectionProvider.registerBehavior(calendarCollection.behaviorContainer)

            val config = Config(viewConfig, monthLabelConfig, dayLabelConfig, dayConfig)
            adapter = MonthAdapter(
                    calendarCollection,
                    viewProperty,
                    selectionProvider,
                    config
            )
            when (this@CalendarView.mode) {
                pagerMode -> {
                    layoutManager = PreLoadLinearLayoutManager(context).apply {
                        orientation = linearLayoutManagerOrientationHorizontal
                    }
                    viewProperty.orientation = ViewProperty.Orientation.Horizontal
                    LinearSnapHelper().attachToRecyclerView(this)
                }
                else -> {
                    layoutManager = PreLoadLinearLayoutManager(context)
                    viewProperty.orientation = ViewProperty.Orientation.Vertical
                }
            }
            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)

            addOnItemTouchListener(selectionProvider)
            if (isStickyHeader) {
                addItemDecoration(StickyItemDecoration(config, viewProperty, stickyHeaderBackgroundColor))
            }

            this@CalendarView.addView(this)
        }
        controller = CalendarController(recyclerView)
    }

    override fun invalidate() {
        super.invalidate()

        val layoutManager = recyclerView.layoutManager as? PreLoadLinearLayoutManager
        layoutManager?.invalidateChildren()

        if (selectionProvider.requestPostInvalidateView()) {
            postInvalidateOnAnimation()
        }
    }

    private fun raiseDaySelected(args: EventArgs) {
        daySelected.raise(this, args)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        viewProperty.width = width
        viewProperty.height = height

        recyclerView.measure(
                MeasureSpec.makeMeasureSpec(width, exactlyMeasureSpec),
                MeasureSpec.makeMeasureSpec(height, exactlyMeasureSpec)
        )
        setMeasuredDimension(width, height)
    }

    // ToDo: No Creation Instance, Recycle
    fun setRange(minDay: IDay, maxDay: IDay) {
        val calendarCollection = CalendarCollection.create(
                minDay = minDay,
                maxDay = maxDay,
                dayOfWeekOrderStart = DayOfWeek.from(this@CalendarView.dayOfWeekOrderStart),
                isShowDaysOfDifferentMonth = this@CalendarView.isShowDaysOfDifferentMonth
        ).apply {
            behaviorContainer.register(dayTextBehavior)
            behaviorContainer.register(dayLabelTextBehavior)
            behaviorContainer.register(monthLabelTextBehavior)
        }
        selectionProvider.registerBehavior(calendarCollection.behaviorContainer)
        monthAdapter.calendar = calendarCollection
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parent = super.onSaveInstanceState()
        val savedState = SavedState(parent)
        savedState.minDay = monthAdapter.calendar.minDay
        savedState.maxDay = monthAdapter.calendar.maxDay

        val bundle = Bundle()
        bundle.putParcelable(savedStateCalendarView, savedState)
        bundle.putParcelable(savedStateLayoutManager, recyclerView.layoutManager?.onSaveInstanceState())
        bundle.putParcelable(savedStateSelectionProvider, selectionProvider.createState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle ?: return

        val savedState: SavedState = bundle.getParcelable(savedStateCalendarView) ?: return
        super.onRestoreInstanceState(savedState.superState)
        val minDay = savedState.minDay
        val maxDay = savedState.maxDay

        if (minDay != null && maxDay != null) {
            setRange(minDay, maxDay)
        }

        recyclerView.layoutManager?.onRestoreInstanceState(bundle.getParcelable(savedStateLayoutManager))

        val selectionProviderState: Bundle = bundle.getParcelable(savedStateSelectionProvider)
                ?: return
        selectionProvider.restoreState(selectionProviderState)
    }

    private class SavedState : BaseSavedState {

        companion object {

            private const val nullValue = 0
            private const val nonNullValue = 1

            // UPPER_SNAKE_CASE: because android architecture restriction
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        var minDay: IDay? = null
        var maxDay: IDay? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            minDay = parcel.readDay()
            maxDay = parcel.readDay()
        }


        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            minDay.writToParcel(parcel)
            maxDay.writToParcel(parcel)
        }

        override fun describeContents(): Int {
            return 0
        }

        private fun IDay?.writToParcel(parcel: Parcel) {
            val day = this
            if (day == null) {
                parcel.writeInt(nullValue)
                return
            } else {
                parcel.writeInt(nonNullValue)
            }
            parcel.writeInt(day.year.toInt())
            parcel.writeByte(day.month)
            parcel.writeByte(day.day)
            parcel.writeInt(day.dayOfWeek.value)
        }

        private fun Parcel.readDay(): IDay? {
            val isNull = readInt() == nullValue
            if (isNull) {
                return null
            }
            val year = readInt().toShort()
            val month = readByte()
            val day = readByte()
            val dayOfWeek = readInt()
            return Day(year, month, day, DayOfWeek.from(dayOfWeek))
        }
    }
}