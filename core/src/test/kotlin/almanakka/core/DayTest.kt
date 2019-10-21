package almanakka.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class DayTest {

    @Test
    fun testEquals() {
        val a = Day(2018, 1, 1, DayOfWeek.firstday)
        val b = Day(2018, 1, 1, DayOfWeek.firstday)

        assertEquals(a == b, true)
    }

    @Test
    fun testNotEquals() {
        val a = Day(2018, 1, 1, DayOfWeek.firstday)
        val b = Day(2017, 1, 1, DayOfWeek.firstday)
        val c = Day(2018, 2, 1, DayOfWeek.firstday)
        val d = Day(2018, 1, 2, DayOfWeek.firstday)
        val e = Day(2018, 1, 1, DayOfWeek.lastday)

        assertEquals(a == b, false)
        assertEquals(a == c, false)
        assertEquals(a == d, false)
        assertEquals(a == e, false)
    }

    @Test
    fun testHashCode() {
        val a = Day(2018, 10, 1, DayOfWeek.firstday)
        // 0000_0111_1110_0010 = 2018
        // 0000_1010 = 10
        val hash = 0b0000_0111_1110_0010___0000_1010___0000_0001

        assertEquals(a.hashCode(), hash)
    }

    @Test
    fun testToString() {
        val a = Day(2018, 10, 1, DayOfWeek.firstday)

        assertEquals(a.toString(), "2018 10/1 dayOfWeek: 1")
    }

    @Test
    fun testCompareTo() {
        val a = Day(2018, 10, 1, DayOfWeek.firstday)
        val moreThanA1 = Day(2018, 10, 2, DayOfWeek.firstday)
        val moreThanA2 = Day(2019, 1, 1, DayOfWeek.firstday)
        val fewThanA1 = Day(2018, 9, 20, DayOfWeek.firstday)
        val fewThanA2 = Day(2017, 12, 1, DayOfWeek.firstday)

        assertEquals(a < moreThanA1, true)
        assertEquals(a < moreThanA2, true)
        assertEquals(fewThanA1 < a, true)
        assertEquals(fewThanA2 < a, true)
    }
}