package almanakka.core

/**
 * [month] value: 1, 2, ..., 12
 */
class Day(
        override val year: Short,
        override val month: Byte,
        override val day: Byte,
        override val dayOfWeek: DayOfWeek) : IDay {

    override var previousDay: Day? = null
        internal set

    override var nextDay: Day? = null
        internal set

    override fun equals(other: Any?): Boolean {
        val otherDay = other as? Day ?: return false
        if (year != otherDay.year) {
            return false
        }
        if (month != otherDay.month) {
            return false
        }
        if (day != otherDay.day) {
            return false
        }
        if (dayOfWeek != otherDay.dayOfWeek) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        return (year.toInt() shl 16) or (month.toInt() shl 8) or day.toInt()
    }

    override fun toString(): String {
        return "$year $month/$day dayOfWeek: ${dayOfWeek.value}"
    }

    override operator fun compareTo(day: IDay): Int {
        return hashCode().compareTo(day.hashCode())
    }
}