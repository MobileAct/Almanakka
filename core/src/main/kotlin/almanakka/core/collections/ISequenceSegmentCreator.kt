package almanakka.core.collections

interface ISequenceSegmentCreator<T> {

    fun createSequenceSegment(startIndex: Int, endIndex: Int): ISequenceSegment<T>

    fun sequenceSegmentLinked(linkedSegment: ISequenceSegment<T>)
}