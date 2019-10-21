package almanakka.core

interface IMonth {

    val year: Short
    val month: Byte
    val weekSize: Int
    val firstWeek: IWeek
    val lastWeek: IWeek

    operator fun get(index: Int): IWeek

    fun asSequence(): Sequence<IWeek>
}