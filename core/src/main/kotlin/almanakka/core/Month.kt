package almanakka.core

class Month(
        override val year: Short,
        override val month: Byte,
        private val weeks: Array<Week>) : IMonth {

    override val weekSize: Int
        get() = weeks.size

    override val firstWeek: Week
        get() = weeks.first()

    override val lastWeek: Week
        get() = weeks.last()

    override operator fun get(index: Int): IWeek {
        return weeks[index]
    }

    override fun asSequence(): Sequence<Week> {
        return weeks.asSequence()
    }

    override fun equals(other: Any?): Boolean {
        val otherMonth = other as? Month ?: return false
        if (year != otherMonth.year) {
            return false
        }
        if (month != otherMonth.month) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        return (year.toInt() shl 8) or month.toInt()
    }

    override fun toString(): String {
        return "year: $year, month: $month"
    }
}