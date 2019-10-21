package almanakka.core.collections

interface ISequenceSegment<T> {

    var segmentOffset: Int
    val size: Int
    val firstIndex: Int
        get() = segmentOffset
    val lastIndex: Int
        get() = segmentOffset + size - 1

    val first: T
    val last: T
    var previousSegment: ISequenceSegment<T>?
    var nextSegment: ISequenceSegment<T>?

    operator fun get(indexOfSegment: Int): T
}