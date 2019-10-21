package almanakka.ui

import almanakka.ui.events.EventArgs
import almanakka.ui.events.EventHandler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarController(private val recyclerView: RecyclerView) {

    private val monthAdapter = recyclerView.adapter as MonthAdapter
    private val layoutManager = recyclerView.layoutManager as LinearLayoutManager

    val scrolled = EventHandler<CalendarController, EventArgs>()

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrolled.raise(this@CalendarController, EventArgs.empty)
            }
        })
    }

    val itemCount: Int
        get() = monthAdapter.itemCount

    fun findFirstVisibleItemPosition(): Int {
        return layoutManager.findFirstVisibleItemPosition()
    }

    fun findFirstCompletelyVisibleItemPosition(): Int {
        return layoutManager.findFirstCompletelyVisibleItemPosition()
    }

    fun findLastVisibleItemPosition(): Int {
        return layoutManager.findLastVisibleItemPosition()
    }

    fun findLastCompletelyVisibleItemPosition(): Int {
        return layoutManager.findLastCompletelyVisibleItemPosition()
    }

    fun findPosition(year: Short, month: Byte): Int? {
        return monthAdapter.calendar.convertMonthToIndex(year, month)
    }

    fun findFirstPosition(year: Short): Int? {
        for (i in 1..12) {
            val result = findPosition(year, i.toByte())
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun scrollToPosition(position: Int) {
        recyclerView.scrollToPosition(position)
    }

    fun smoothScrollToPosition(position: Int) {
        recyclerView.smoothScrollToPosition(position)
    }

    fun scrollToNextYear() {
        val firstVisibleMonth = monthAdapter.calendar[findFirstVisibleItemPosition()]
        val nextYearPosition = findFirstPosition((firstVisibleMonth.year + 1).toShort()) ?: return
        scrollToPosition(nextYearPosition)
    }

    fun smoothScrollToNextYear() {
        val firstVisibleMonth = monthAdapter.calendar[findFirstVisibleItemPosition()]
        val nextYearPosition = findFirstPosition((firstVisibleMonth.year + 1).toShort()) ?: return
        smoothScrollToPosition(nextYearPosition)
    }

    fun scrollToPreviousYear() {
        val firstVisibleMonth = monthAdapter.calendar[findFirstVisibleItemPosition()]
        val previousYearPosition = findFirstPosition((firstVisibleMonth.year - 1).toShort())
                ?: return
        scrollToPosition(previousYearPosition)
    }

    fun smoothScrollToPreviousYear() {
        val firstVisibleMonth = monthAdapter.calendar[findFirstVisibleItemPosition()]
        val previousYearPosition = findFirstPosition((firstVisibleMonth.year - 1).toShort())
                ?: return
        smoothScrollToPosition(previousYearPosition)
    }
}