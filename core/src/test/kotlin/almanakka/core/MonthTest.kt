package almanakka.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonthTest {

    @Test
    fun testEquals() {
        // month is not checked Month.weeks when equals()
        val a = Month(2018, 1, emptyArray())
        val b = Month(2018, 1, emptyArray())

        assertEquals(a == b, true)
    }

    @Test
    fun testNotEquals() {
        val a = Month(2018, 1, emptyArray())
        val b = Month(2019, 1, emptyArray())
        val c = Month(2018, 2, emptyArray())

        assertEquals(a == b, false)
        assertEquals(a == c, false)
    }

    @Test
    fun testHashCode() {
        val a = Month(2018, 1, emptyArray())
        // 0000_0111_1110_0010 = 2018
        val hash = 0b0000_0111_1110_0010___0000_0001

        assertEquals(a.hashCode(), hash)
    }

    @Test
    fun testToString() {
        val a = Month(2018, 1, emptyArray())

        assertEquals(a.toString(), "year: 2018, month: 1")
    }
}