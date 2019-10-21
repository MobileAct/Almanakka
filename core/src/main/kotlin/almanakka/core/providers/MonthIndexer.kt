package almanakka.core.providers

import almanakka.core.IDay

class MonthIndexer(private val minDay: IDay, private val maxDay: IDay) {

    val size: Int = calculateMonthSize()

    private fun calculateMonthSize(): Int {
        val dYear = maxDay.year - minDay.year
        val dMonth = maxDay.month - minDay.month

        return dYear * 12 + dMonth + 1
    }

    fun convertMonthToIndex(year: Short, month: Byte): Int? {
        if (year < minDay.year || (year == minDay.year && month < minDay.month)) {
            return null
        }
        if (maxDay.year < year || (year == maxDay.year && maxDay.month < month)) {
            return null
        }

        return (year - minDay.year) * 12 + month - minDay.month
    }

    /**
     * return pair is year and month
     */
    fun convertIndexToMonth(index: Int): Pair<Short, Byte>? {
        if (index < 0) {
            return null
        }
        if (size <= index) {
            return null
        }

        var size = index
        val dYear = size / 12
        size = index % 12

        var year = minDay.year + dYear
        var month = minDay.month + size

        if (12 < month) {
            month -= 12
            year += 1
        }

        return Pair(year.toShort(), month.toByte())
    }
}