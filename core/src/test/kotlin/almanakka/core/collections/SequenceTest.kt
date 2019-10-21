package almanakka.core.collections

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SequenceTest {

    class SequenceSegmentCreator : ISequenceSegmentCreator<Int> {

        var startIndexOfCreateSequenceSegmentArgument: Int? = null
        var endIndexOfCreateSequenceSegmentArgument: Int? = null
        var resultOfCreateSequence: ISequenceSegment<Int>? = null
        var linkedSegmentOfSequenceSegmentLinkedArgument: ISequenceSegment<Int>? = null

        override fun createSequenceSegment(startIndex: Int, endIndex: Int): ISequenceSegment<Int> {
            startIndexOfCreateSequenceSegmentArgument = startIndex
            endIndexOfCreateSequenceSegmentArgument = endIndex

            val result = SequenceSegment.create((startIndex..endIndex).toList().toTypedArray())
            resultOfCreateSequence = result
            return result
        }

        override fun sequenceSegmentLinked(linkedSegment: ISequenceSegment<Int>) {
            linkedSegmentOfSequenceSegmentLinkedArgument = linkedSegment
        }
    }

    private fun assertSegmentSize(sequence: Sequence<Int>, expectSegmentSize: Int) {
        assert(sequence.segmentSize == expectSegmentSize) {
            "segmentSize is ${sequence.segmentSize}, not equal $expectSegmentSize"
        }
    }

    private fun assertTopSegmentSize(sequence: Sequence<Int>, expectTopSegmentSize: Int) {
        assert(sequence.topSegmentSize == expectTopSegmentSize) {
            "topSegmentSize is ${sequence.topSegmentSize}, not equal $expectTopSegmentSize"
        }
    }

    private fun assertValues(sequence: Sequence<Int>, indexes: IntRange) {
        for (i in indexes) {
            assert(sequence[i] == i)
        }
    }

    @Test
    fun testCreate_simpleCreate1() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(0, 1)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 0)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 0)

        assertSegmentSize(sequence, 1)
        assertTopSegmentSize(sequence, 1)
        assert(sequence[0] == 0)
    }

    @Test
    fun testCreate_simpleCreate2() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(4, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 2)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 6)

        assertSegmentSize(sequence, 1)
        assertTopSegmentSize(sequence, 1)
        assertValues(sequence, 2..6)
    }

    @Test
    fun testCreate_simpleCreate3() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(99, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 97)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 99)

        assertSegmentSize(sequence, 1)
        assertTopSegmentSize(sequence, 1)
        assertValues(sequence, 97..99)
    }

    @Test
    fun testCreate_failValidArgument() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            sequence.create(0, 0)
        }
    }

    @Test
    fun testCreate_failValidSegment() {
        val sequenceSegmentCreator = object : ISequenceSegmentCreator<Int> {

            override fun createSequenceSegment(startIndex: Int, endIndex: Int): ISequenceSegment<Int> {
                return SequenceSegment.create(arrayOf(1, 2, 3))
            }

            override fun sequenceSegmentLinked(linkedSegment: ISequenceSegment<Int>) {
            }
        }
        val sequence = Sequence(sequenceSegmentCreator, 100)

        Assertions.assertThrows(IllegalStateException::class.java) {
            sequence.create(0, 1)
        }
    }

    @Test
    fun testCreate_duplicate() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(0, 3)

        sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument = null
        sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument = null
        sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument = null

        sequence.create(0, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == null)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == null)
        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)
    }

    @Test
    fun testCreate_createIndexRange_aroundZero() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(0, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 0)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 2)
    }

    @Test
    fun testCreate_createIndexRange_aroundFrontSegment() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(0, 3)
        sequence.create(3, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 3)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 5)
    }

    @Test
    fun testCreate_createIndexRange_aroundMaxSize() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(99, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 97)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 99)
    }

    @Test
    fun testCreate_createIndexRange_aroundBehindSegment() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(10, 3)
        sequence.create(7, 3)

        assert(sequenceSegmentCreator.startIndexOfCreateSequenceSegmentArgument == 5)
        assert(sequenceSegmentCreator.endIndexOfCreateSequenceSegmentArgument == 7)
    }

    @Test
    fun testCreate_segmentOffset() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(4, 2)

        assert(sequenceSegmentCreator.resultOfCreateSequence?.segmentOffset == 3)
    }

    @Test
    fun testCreate_linkSegment_bothSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(3, 3) // 1, 2, 3, 4, 5
        sequence.create(9, 3) // 7, 8, 9, 10, 11

        assert(sequence.topSegmentSize == 2)
        assert(sequence.segmentSize == 2)

        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)

        sequence.create(6, 3)

        val linkedSegment = checkNotNull(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument)
        val previousSegment = checkNotNull(linkedSegment.previousSegment)
        val nextSegment = checkNotNull(linkedSegment.nextSegment)

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 3)

        assert(linkedSegment.segmentOffset == 6)
        assert(linkedSegment.size == 1)
        assert(previousSegment.segmentOffset == 1)
        assert(previousSegment.size == 5)
        assert(nextSegment.segmentOffset == 7)
        assert(nextSegment.size == 5)
    }

    @Test
    fun testCreate_linkSegment_notBothSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(3, 3) // 1, 2, 3, 4, 5
        sequence.create(11, 3) // 9, 10, 11, 12, 13

        assert(sequence.topSegmentSize == 2)
        assert(sequence.segmentSize == 2)

        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)

        sequence.create(7, 1)

        assert(sequence.topSegmentSize == 3)
        assert(sequence.segmentSize == 3)

        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)
    }

    @Test
    fun testCreate_linkSegment_frontSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(3, 3) // 1, 2, 3, 4, 5

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 1)

        sequence.create(6, 3) // 6, 7, 8

        val linkedSegment = checkNotNull(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument)
        val previousSegment = checkNotNull(linkedSegment.previousSegment)

        assert(linkedSegment.nextSegment == null)

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 2)

        assert(linkedSegment.segmentOffset == 6)
        assert(linkedSegment.size == 3)
        assert(previousSegment.segmentOffset == 1)
        assert(previousSegment.size == 5)
    }

    @Test
    fun testCreate_linkSegment_notFrontSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(3, 3) // 1, 2, 3, 4, 5

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 1)

        sequence.create(9, 3) // 7, 8, 9, 10, 11

        assert(sequence.topSegmentSize == 2)
        assert(sequence.segmentSize == 2)

        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)
    }

    @Test
    fun testCreate_linkSegment_behindSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(8, 3) // 6, 7, 8, 9, 10

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 1)

        sequence.create(3, 3) // 1, 2, 3, 4, 5

        val linkedSegment = checkNotNull(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument)
        val nextSegment = checkNotNull(linkedSegment.nextSegment)

        assert(linkedSegment.previousSegment == null)

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 2)

        assert(linkedSegment.segmentOffset == 1)
        assert(linkedSegment.size == 5)
        assert(nextSegment.segmentOffset == 6)
        assert(nextSegment.size == 5)
    }

    @Test
    fun testCreate_linkSegment_notBehindSide() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(9, 3) // 7, 8, 9, 10, 11

        assert(sequence.topSegmentSize == 1)
        assert(sequence.segmentSize == 1)

        sequence.create(3, 3) // 1, 2, 3, 4, 5

        assert(sequence.topSegmentSize == 2)
        assert(sequence.segmentSize == 2)

        assert(sequenceSegmentCreator.linkedSegmentOfSequenceSegmentLinkedArgument == null)
    }

    @Test
    fun testGet_failOutOfRange() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.create(50, 100)

        Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
            sequence[-1]
        }

        Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
            sequence[100]
        }
    }

    @Test
    fun testGet_failNotCreated() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        Assertions.assertThrows(IllegalStateException::class.java) {
            sequence[10]
        }
    }

    @Test
    fun testGetOrCreate() {
        val sequenceSegmentCreator = SequenceSegmentCreator()
        val sequence = Sequence(sequenceSegmentCreator, 100)

        sequence.getOrCreate(3, 10)
    }
}