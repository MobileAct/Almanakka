package almanakka.core.providers

import almanakka.core.*
import almanakka.core.extensions.toDay
import java.util.*
import java.util.Calendar.DAY_OF_MONTH as jCalendarDayOfMonth
import java.util.Calendar.MONTH as jCalendarMonth

class MonthCreator : IMonthCreator {

    companion object {

        private const val daySizeOfWeek = 7
    }

    private val calendar: ICalendar
    private val dayConnector: IDayConnector
    var dayOfWeekOrderStart: DayOfWeek = DayOfWeek.firstday

    constructor(dayConnector: IDayConnector) : this(GregorianCalendar(), dayConnector)

    constructor(calendar: ICalendar, dayConnector: IDayConnector) {
        this.calendar = calendar
        this.dayConnector = dayConnector
    }

    override fun create(year: Short, month: Byte): Month {
        val daysOfMonth = createDays(year, month).toList()
        linkDays(daysOfMonth)
        val frontNullDays = createFrontNullDaySequence(daysOfMonth.first())
        val behindNullDays = createBehindNullDaySequence(daysOfMonth.last())

        // ToDo: improve performance, to allow mutable list
        val weeks = (frontNullDays + daysOfMonth + behindNullDays)
                .chunked(daySizeOfWeek) { Week(it.toTypedArray()) }
                .toList()
                .toTypedArray()
        return Month(year, month, weeks)
    }

    private fun createDays(year: Short, month: Byte): Sequence<Day> {
        calendar.set(year.toInt(), month.toInt() - 1, 1)

        return sequence {
            while (calendar.get(jCalendarMonth) == month.toInt() - 1) {
                yield(calendar.toDay())
                calendar.add(jCalendarDayOfMonth, 1)
            }
        }
    }

    private fun linkDays(days: IList<Day>) {
        for (i in 0 until (days.size - 1)) {
            val frontDay = days[i]
            val behindDay = days[i + 1]

            dayConnector.connect(frontDay, behindDay)
        }
    }

    private fun createFrontNullDaySequence(firstDayOfMonth: Day): Sequence<Day?> {
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek
        val nullDays = if (firstDayOfWeek.value < dayOfWeekOrderStart.value)
            (7 - dayOfWeekOrderStart.value) + firstDayOfWeek.value
        else
            firstDayOfWeek.value - dayOfWeekOrderStart.value

        return createNullDaySequence(nullDays)
    }

    private fun createBehindNullDaySequence(lastDayOfMonth: Day): Sequence<Day?> {
        val lastDayOfWeek = lastDayOfMonth.dayOfWeek
        val previousDayOfWeekOrderStart = dayOfWeekOrderStart.previousDayOfWeek()
        val nullDays = if (previousDayOfWeekOrderStart.value < lastDayOfWeek.value)
            7 - lastDayOfWeek.value + previousDayOfWeekOrderStart.value
        else
            previousDayOfWeekOrderStart.value - lastDayOfWeek.value

        return createNullDaySequence(nullDays)
    }

    private fun createNullDaySequence(nullDays: Int): Sequence<Day?> {
        if (nullDays <= 0) {
            return emptySequence()
        }

        return sequence {
            for (i in 0 until nullDays) {
                yield(null)
            }
        }
    }

    override fun createDayOfWeekOrders(): Array<DayOfWeek> {
        return generateSequence(dayOfWeekOrderStart) { it.nextDayOfWeek() }
                .take(7)
                .toList()
                .toTypedArray()
    }
}