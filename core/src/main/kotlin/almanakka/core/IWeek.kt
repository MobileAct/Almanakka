package almanakka.core

interface IWeek {

    val daySize: Int
    val firstDay: IDay
    val lastDay: IDay

    operator fun get(index: Int): IDay?

    fun asSequence(): Sequence<IDay?>
}