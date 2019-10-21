package almanakka.core

interface IDay {

    val year: Short
    val month: Byte
    val day: Byte
    val dayOfWeek: DayOfWeek
    val previousDay: IDay?
    val nextDay: IDay?

    operator fun compareTo(day: IDay): Int

    fun toImmutable(): ImmutableDay {
        return ImmutableDay(year, month, day)
    }
}