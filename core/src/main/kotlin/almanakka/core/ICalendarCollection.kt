package almanakka.core

import almanakka.core.behaviors.IBehaviorContainer

interface ICalendarCollection {

    val minDay: IDay
    val maxDay: IDay
    val dayOfWeekOrders: Array<DayOfWeek>
    val size: Int

    val behaviorContainer: IBehaviorContainer

    operator fun get(index: Int): IMonth

    fun convertMonthToIndex(year: Short, month: Byte): Int?

    fun findMonth(year: Short, month: Byte): IMonth? {
        val index = convertMonthToIndex(year, month) ?: return null
        return get(index)
    }

    fun findDay(year: Short, month: Byte, day: Byte): IDay? {
        val foundMonth = findMonth(year, month) ?: return null
        return foundMonth
                .asSequence()
                .flatMap { it.asSequence() }
                .filterNotNull()
                .firstOrNull { it.month == month && it.day == day }
    }
}