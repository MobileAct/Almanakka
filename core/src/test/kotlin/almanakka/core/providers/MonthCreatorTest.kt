package almanakka.core.providers

import almanakka.core.Day
import almanakka.core.DayOfWeek
import org.junit.jupiter.api.Test
import java.util.*

class MonthCreatorTest {

    private fun assertContinuousDays(days: Sequence<Day>) {
        val year = days.first().year
        val month = days.first().month

        var dayOfWeek = days.first().dayOfWeek
        var previousDay: Day? = null
        for ((i, day) in days.withIndex()) {
            assert(day.day.toInt() == i + 1)
            assert(day.dayOfWeek == dayOfWeek)

            assert(day.previousDay == previousDay)
            if (previousDay != null) {
                assert(previousDay.nextDay == day)
            }

            dayOfWeek = dayOfWeek.nextDayOfWeek()
            previousDay = day
        }

        assert(days.last().nextDay == null)
    }

    private fun assertFrontNullDays(days: Sequence<Day?>, nullDays: Int) {
        assert(days.takeWhile { it == null }.count() == nullDays)
    }

    private fun assertBehindNullDays(days: Sequence<Day?>, nullDays: Int) {
        assert(days.toList().asReversed().takeWhile { it == null }.count() == nullDays)
    }

    private fun IMonthCreator.testMonth(year: Short, month: Byte, weekSize: Int, frontNullDays: Int, behindNullDays: Int) {
        val createdMonth = create(year, month)

        assert(createdMonth.weekSize == weekSize)

        for (week in createdMonth.asSequence()) {
            assert(week.days.size == 7) {
                week.days.joinToString(",")
            }
        }

        val days = createdMonth.asSequence().flatMap { it.days.asSequence() }

        assertFrontNullDays(days, frontNullDays)
        assertContinuousDays(days.filterNotNull())
        assertBehindNullDays(days, behindNullDays)
    }

    @Test
    fun test_simple() {
        val monthCreator = MonthCreator(GregorianCalendar(), DayConnector())

        // 2019/1/1 is tuesday, 2019/1/31 is thursday
        monthCreator.testMonth(2019, 1, 5, 2, 2)
        // 2019/2/1 is friday, 2019/2/28 is thursday
        monthCreator.testMonth(2019, 2, 5, 5, 2)
        // 2019/3/1 is friday, 2019/3/31 is sunday
        monthCreator.testMonth(2019, 3, 6, 5, 6)
        // 2019/4/1 is monday, 2019/4/30 is tuesday
        monthCreator.testMonth(2019, 4, 5, 1, 4)
        // 2019/5/1 is wednesday, 2019/5/31 is friday
        monthCreator.testMonth(2019, 5, 5, 3, 1)
        // 2019/6/1 is saturday, 2019/6/30 is monday
        monthCreator.testMonth(2019, 6, 6, 6, 6)
        // 2019/7/1 is monday, 2019/7/31 is wednesday
        monthCreator.testMonth(2019, 7, 5, 1, 3)
        // 2019/8/1 is thursday, 2019/8/31 is saturday
        monthCreator.testMonth(2019, 8, 5, 4, 0)
        // 2019/9/1 is sunday, 2019/9/30 is monday
        monthCreator.testMonth(2019, 9, 5, 0, 5)
        // 2019/10/1 is tuesday, 2019/10/31 is thursday
        monthCreator.testMonth(2019, 10, 5, 2, 2)
        // 2019/11/1 is friday, 2019/11/30 is saturday
        monthCreator.testMonth(2019, 11, 5, 5, 0)
        // 2019/12/1 is sunday, 2019/12/31 is tuesday
        monthCreator.testMonth(2019, 12, 5, 0, 4)
    }

    @Test
    fun test_dayOfWeekOrderStart_monday() {
        val monthCreator = MonthCreator(GregorianCalendar(), DayConnector()).apply {
            dayOfWeekOrderStart = DayOfWeek.Monday
        }

        // 2019/1/1 is tuesday, 2019/1/31 is thursday
        monthCreator.testMonth(2019, 1, 5, 1, 3)
        // 2019/2/1 is friday, 2019/2/28 is thursday
        monthCreator.testMonth(2019, 2, 5, 4, 3)
        // 2019/3/1 is friday, 2019/3/31 is sunday
        monthCreator.testMonth(2019, 3, 5, 4, 0)
        // 2019/4/1 is monday, 2019/4/30 is tuesday
        monthCreator.testMonth(2019, 4, 5, 0, 5)
        // 2019/5/1 is wednesday, 2019/5/31 is friday
        monthCreator.testMonth(2019, 5, 5, 2, 2)
        // 2019/6/1 is saturday, 2019/6/30 is monday
        monthCreator.testMonth(2019, 6, 5, 5, 0)
        // 2019/7/1 is monday, 2019/7/31 is wednesday
        monthCreator.testMonth(2019, 7, 5, 0, 4)
        // 2019/8/1 is thursday, 2019/8/31 is saturday
        monthCreator.testMonth(2019, 8, 5, 3, 1)
        // 2019/9/1 is sunday, 2019/9/30 is monday
        monthCreator.testMonth(2019, 9, 6, 6, 6)
        // 2019/10/1 is tuesday, 2019/10/31 is thursday
        monthCreator.testMonth(2019, 10, 5, 1, 3)
        // 2019/11/1 is friday, 2019/11/30 is saturday
        monthCreator.testMonth(2019, 11, 5, 4, 1)
        // 2019/12/1 is sunday, 2019/12/31 is tuesday
        monthCreator.testMonth(2019, 12, 6, 6, 5)
    }
}