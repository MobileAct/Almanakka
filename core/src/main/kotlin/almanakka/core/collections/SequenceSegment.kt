package almanakka.core.collections

class SequenceSegment<T> private constructor(private val elements: Array<T>) : ISequenceSegment<T> {

    companion object {

        fun <T> create(elements: Array<T>): ISequenceSegment<T> {
            if (elements.isEmpty()) {
                throw IllegalArgumentException("sequence segment must not be empty")
            }
            return SequenceSegment(elements)
        }
    }

    override var segmentOffset: Int = 0
    override val size = elements.size
    override val first: T = elements.first()
    override val last: T = elements.last()

    override var previousSegment: ISequenceSegment<T>? = null
    override var nextSegment: ISequenceSegment<T>? = null

    override fun get(indexOfSegment: Int): T {
        return elements[indexOfSegment]
    }
}