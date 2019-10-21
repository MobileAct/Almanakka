package almanakka.core.providers

import almanakka.core.Day
import almanakka.core.DayOfWeek
import almanakka.core.Month
import almanakka.core.Week
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MonthConnectorTest {

    @Test
    fun testConnect_success() {
        val monthConnector = MonthConnector()

        val day1 = Day(1, 1, 1, DayOfWeek.Sunday)
        val day2 = Day(1, 1, 2, DayOfWeek.Monday)
        val day3 = Day(1, 1, 3, DayOfWeek.Tuesday)
        val day4 = Day(1, 1, 4, DayOfWeek.Wednesday)
        val day5 = Day(1, 1, 5, DayOfWeek.Thursday)
        val day6 = Day(1, 1, 6, DayOfWeek.Friday)
        val day7 = Day(1, 1, 7, DayOfWeek.Saturday)

        val frontWeek = Week(arrayOf(day1, day2, day3, null, null, null, null))
        val behindWeek = Week(arrayOf(null, null, null, day4, day5, day6, day7))
        val emptyWeek = Week(emptyArray())
        val frontMonth = Month(1, 1, arrayOf(emptyWeek, frontWeek))
        val behindMonth = Month(1, 1, arrayOf(behindWeek, emptyWeek))

        monthConnector.connect(frontMonth, behindMonth)

        assert(frontWeek.days[0] == day1)
        assert(frontWeek.days[1] == day2)
        assert(frontWeek.days[2] == day3)
        assert(frontWeek.days[3] == day4)
        assert(frontWeek.days[4] == day5)
        assert(frontWeek.days[5] == day6)
        assert(frontWeek.days[6] == day7)

        assert(behindWeek.days[0] == day1)
        assert(behindWeek.days[1] == day2)
        assert(behindWeek.days[2] == day3)
        assert(behindWeek.days[3] == day4)
        assert(behindWeek.days[4] == day5)
        assert(behindWeek.days[5] == day6)
        assert(behindWeek.days[6] == day7)
    }

    @Test
    fun testConnect_invalidDaysSize() {
        val monthConnector = MonthConnector()

        val day1 = Day(1, 1, 1, DayOfWeek.Sunday)
        val day2 = Day(1, 1, 2, DayOfWeek.Monday)
        val day3 = Day(1, 1, 3, DayOfWeek.Tuesday)
        val day4 = Day(1, 1, 4, DayOfWeek.Wednesday)
        val day5 = Day(1, 1, 5, DayOfWeek.Thursday)
        val day6 = Day(1, 1, 6, DayOfWeek.Friday)
        val day7 = Day(1, 1, 7, DayOfWeek.Saturday)

        val frontWeek = Week(arrayOf(day1, day2, day3))
        val behindWeek = Week(arrayOf(null, null, null, day4, day5, day6, day7))
        val frontMonth = Month(1, 1, arrayOf(frontWeek))
        val behindMonth = Month(1, 1, arrayOf(behindWeek))

        Assertions.assertThrows(IllegalStateException::class.java) {
            monthConnector.connect(frontMonth, behindMonth)
        }
    }

    @Test
    fun testConnect_invalidXorDays1() {
        val monthConnector = MonthConnector()

        val day1 = Day(1, 1, 1, DayOfWeek.Sunday)
        val day2 = Day(1, 1, 2, DayOfWeek.Monday)
        val day3 = Day(1, 1, 3, DayOfWeek.Tuesday)
        val day4 = Day(1, 1, 4, DayOfWeek.Wednesday)
        val day5 = Day(1, 1, 5, DayOfWeek.Thursday)
        val day6 = Day(1, 1, 6, DayOfWeek.Friday)
        val day7 = Day(1, 1, 7, DayOfWeek.Saturday)

        val frontWeek = Week(arrayOf(day1, day2, day3, day4, null, null, null))
        val behindWeek = Week(arrayOf(null, null, null, day4, day5, day6, day7))
        val frontMonth = Month(1, 1, arrayOf(frontWeek))
        val behindMonth = Month(1, 1, arrayOf(behindWeek))

        Assertions.assertThrows(IllegalStateException::class.java) {
            monthConnector.connect(frontMonth, behindMonth)
        }
    }

    @Test
    fun testConnect_invalidXorDays2() {
        val monthConnector = MonthConnector()

        val day1 = Day(1, 1, 1, DayOfWeek.Sunday)
        val day2 = Day(1, 1, 2, DayOfWeek.Monday)
        val day3 = Day(1, 1, 3, DayOfWeek.Tuesday)
        val day4 = Day(1, 1, 4, DayOfWeek.Wednesday)
        val day5 = Day(1, 1, 5, DayOfWeek.Thursday)
        val day6 = Day(1, 1, 6, DayOfWeek.Friday)

        val frontWeek = Week(arrayOf(day1, day2, day3, null, null, null, null))
        val behindWeek = Week(arrayOf(null, null, null, day4, day5, day6, null))
        val frontMonth = Month(1, 1, arrayOf(frontWeek))
        val behindMonth = Month(1, 1, arrayOf(behindWeek))

        Assertions.assertThrows(IllegalStateException::class.java) {
            monthConnector.connect(frontMonth, behindMonth)
        }
    }

    @Test
    fun testConnect_fullDays() {
        val monthConnector = MonthConnector()

        val day1 = Day(1, 1, 1, DayOfWeek.Sunday)
        val day2 = Day(1, 1, 2, DayOfWeek.Monday)
        val day3 = Day(1, 1, 3, DayOfWeek.Tuesday)
        val day4 = Day(1, 1, 4, DayOfWeek.Wednesday)
        val day5 = Day(1, 1, 5, DayOfWeek.Thursday)
        val day6 = Day(1, 1, 6, DayOfWeek.Friday)
        val day7 = Day(1, 1, 7, DayOfWeek.Saturday)

        val frontWeek = Week(arrayOf(day1, day2, day3, day4, day5, day6, day7))
        val behindWeek = Week(arrayOf(day1, day2, day3, day4, day5, day6, day7))
        val emptyWeek = Week(emptyArray())
        val frontMonth = Month(1, 1, arrayOf(emptyWeek, frontWeek))
        val behindMonth = Month(1, 1, arrayOf(behindWeek, emptyWeek))

        monthConnector.connect(frontMonth, behindMonth)
    }
}