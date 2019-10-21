package almanakka.core

class Week(val days: Array<Day?>) : IWeek {

    override val daySize: Int
        get() = days.size

    override val firstDay: Day
        get() = days.filterNotNull().first()

    override val lastDay: Day
        get() = days.filterNotNull().last()

    override operator fun get(index: Int): IDay? {
        return days[index]
    }

    override fun asSequence(): Sequence<Day?> {
        return days.asSequence()
    }
}