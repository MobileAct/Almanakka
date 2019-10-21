package almanakka.core.extensions

import almanakka.core.Day
import almanakka.core.DayOfWeek
import java.util.Calendar as JCalendar

fun JCalendar.toDay(): Day {
    val year = get(java.util.Calendar.YEAR).toShort()
    val month = (get(java.util.Calendar.MONTH) + 1).toByte() // JCalendar month start with 0, 1, 2, ..., 11
    val day = get(java.util.Calendar.DAY_OF_MONTH).toByte()
    val dayOfWeek = get(java.util.Calendar.DAY_OF_WEEK)

    return Day(year, month, day, DayOfWeek.from(dayOfWeek))
}