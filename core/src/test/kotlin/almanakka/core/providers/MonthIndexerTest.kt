package almanakka.core.providers

import almanakka.core.Day
import almanakka.core.DayOfWeek
import org.junit.jupiter.api.Test

class MonthIndexerTest {

    // index : (year, month)
    private val indexTable = listOf<Pair<Int, Pair<Int, Int>>>(
            Pair(0, Pair(2018, 3)),
            Pair(1, Pair(2018, 4)),
            Pair(2, Pair(2018, 5)),
            Pair(3, Pair(2018, 6)),
            Pair(4, Pair(2018, 7)),
            Pair(5, Pair(2018, 8)),
            Pair(6, Pair(2018, 9)),
            Pair(7, Pair(2018, 10)),
            Pair(8, Pair(2018, 11)),
            Pair(9, Pair(2018, 12)),
            Pair(10, Pair(2019, 1)),
            Pair(11, Pair(2019, 2)),
            Pair(12, Pair(2019, 3)),
            Pair(13, Pair(2019, 4)),
            Pair(14, Pair(2019, 5))
    )

    @Test
    fun testSize_oneFullYear() {
        val monthIndexer = MonthIndexer(Day(2019, 1, 1, DayOfWeek.firstday), Day(2019, 12, 1, DayOfWeek.firstday))

        assert(monthIndexer.size == 12)
    }

    @Test
    fun testSize_oneYear() {
        val monthIndexer = MonthIndexer(Day(2019, 2, 1, DayOfWeek.firstday), Day(2019, 4, 1, DayOfWeek.firstday))

        assert(monthIndexer.size == 3)
    }

    @Test
    fun testSize_twoFullYear() {
        val monthIndexer = MonthIndexer(Day(2018, 1, 1, DayOfWeek.firstday), Day(2019, 12, 1, DayOfWeek.firstday))

        assert(monthIndexer.size == 24)
    }

    @Test
    fun testSize_twoYear1() {
        val monthIndexer = MonthIndexer(Day(2018, 3, 1, DayOfWeek.firstday), Day(2019, 5, 1, DayOfWeek.firstday))

        assert(monthIndexer.size == 15)
    }

    @Test
    fun testSize_twoYear2() {
        val monthIndexer = MonthIndexer(Day(2018, 5, 1, DayOfWeek.firstday), Day(2019, 3, 1, DayOfWeek.firstday))

        assert(monthIndexer.size == 11)
    }

    @Test
    fun testConvertMonthToIndex_firstIndex() {
        val monthIndexer = MonthIndexer(Day(2019, 1, 1, DayOfWeek.firstday), Day(2019, 12, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertMonthToIndex(2019, 1) == 0)
    }

    @Test
    fun testConvertMonthToIndex_lastIndex1() {
        val monthIndexer = MonthIndexer(Day(2019, 1, 1, DayOfWeek.firstday), Day(2019, 12, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertMonthToIndex(2019, 12) == 11)
    }

    @Test
    fun testConvertMonthToIndex_lastIndex2() {
        val monthIndexer = MonthIndexer(Day(2019, 3, 1, DayOfWeek.firstday), Day(2020, 2, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertMonthToIndex(2020, 2) == 11)
    }

    @Test
    fun testConvertMonthToIndex_indexTable() {
        val monthIndexer = MonthIndexer(Day(2018, 3, 1, DayOfWeek.firstday), Day(2019, 5, 1, DayOfWeek.firstday))

        for ((i, d) in indexTable) {
            val (year, month) = d

            assert(monthIndexer.convertMonthToIndex(year.toShort(), month.toByte()) == i)
        }
    }

    @Test
    fun testConvertMonthToIndex_outOfRange() {
        val monthIndexer = MonthIndexer(Day(2018, 3, 1, DayOfWeek.firstday), Day(2019, 5, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertMonthToIndex(2018, 2) == null)
        assert(monthIndexer.convertMonthToIndex(2017, 2) == null)
        assert(monthIndexer.convertMonthToIndex(2019, 6) == null)
        assert(monthIndexer.convertMonthToIndex(2020, 6) == null)
    }

    @Test
    fun testConvertIndexToMonth_firstIndex() {
        val monthIndexer = MonthIndexer(Day(2018, 1, 1, DayOfWeek.firstday), Day(2019, 1, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertIndexToMonth(0) == Pair<Short, Byte>(2018, 1))
    }

    @Test
    fun testConvertIndexToMonth_lastIndex1() {
        val monthIndexer = MonthIndexer(Day(2019, 1, 1, DayOfWeek.firstday), Day(2019, 12, 1, DayOfWeek.firstday))
        assert(monthIndexer.convertIndexToMonth(11) == Pair<Short, Byte>(2019, 12))
    }

    @Test
    fun testConvertIndexToMonth_lastIndex2() {
        val monthIndexer = MonthIndexer(Day(2019, 3, 1, DayOfWeek.firstday), Day(2020, 2, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertIndexToMonth(11) == Pair<Short, Byte>(2020, 2))
    }

    @Test
    fun testConvertIndexToMonth_indexTable() {
        val monthIndexer = MonthIndexer(Day(2018, 3, 1, DayOfWeek.firstday), Day(2019, 5, 1, DayOfWeek.firstday))

        for ((i, d) in indexTable) {
            val (year, month) = d

            assert(monthIndexer.convertIndexToMonth(i) == Pair(year.toShort(), month.toByte()))
        }
    }

    @Test
    fun testConvertIndexToMonth_outOfRange() {
        val monthIndexer = MonthIndexer(Day(2018, 3, 1, DayOfWeek.firstday), Day(2019, 5, 1, DayOfWeek.firstday))

        assert(monthIndexer.convertIndexToMonth(-1) == null)
        assert(monthIndexer.convertIndexToMonth(15) == null)
    }
}