package almanakka.core.collections

import almanakka.core.IMutableList

class Sequence<T>(private val sequenceSegmentCreator: ISequenceSegmentCreator<T>, override val size: Int) : ISequence<T> {

    /**
     * First segment of linked segments, there are sorted linear struct
     */
    private val topSegments: IMutableList<ISequenceSegment<T>> = mutableListOf()

    val topSegmentSize: Int
        get() = topSegments.size

    val segmentSize: Int
        get() = segmentSequence().count()

    override fun create(index: Int, createSideRangeIfIndexNotCreated: Int) {
        checkIndex(index)

        if (createSideRangeIfIndexNotCreated <= 0) {
            throw IllegalArgumentException("createSideRangeIfIndexNotCreated must be more than 0")
        }

        val nearbySegmentForStart = getNearbySegmentForStart(index)
        val nearbySegmentForEnd = getNearbySegmentForEnd(index)

        if (nearbySegmentForStart != null && nearbySegmentForStart == nearbySegmentForEnd) {
            // segment is created
            return
        }

        val nearestStartIndex = nearbySegmentForStart?.lastIndex
        val nearestEndIndex = nearbySegmentForEnd?.firstIndex

        fun calculateCreateStartIndex(): Int {
            val sideRangeWithoutTargetIndex = createSideRangeIfIndexNotCreated - 1
            if (nearestStartIndex == null) {
                return Math.max(index - sideRangeWithoutTargetIndex, 0)
            } else {
                return Math.max(index - sideRangeWithoutTargetIndex, nearestStartIndex + 1)
            }
        }

        fun calculateCreateEndIndex(): Int {
            val sideRangeWithoutTargetIndex = createSideRangeIfIndexNotCreated - 1
            if (nearestEndIndex == null) {
                return Math.min(index + sideRangeWithoutTargetIndex, size - 1)
            } else {
                return Math.min(index + sideRangeWithoutTargetIndex, nearestEndIndex - 1)
            }
        }

        val createStartIndex = calculateCreateStartIndex()
        val createEndIndex = calculateCreateEndIndex()
        val createdSegment = sequenceSegmentCreator.createSequenceSegment(
                createStartIndex,
                createEndIndex
        )
        if (createdSegment.size != createEndIndex - createStartIndex + 1) {
            throw IllegalStateException("ISequenceSegmentCreator must create ISequenceSegment by argument index")
        }
        createdSegment.segmentOffset = createStartIndex

        when {
            nearbySegmentForStart != null && nearbySegmentForEnd != null
                    && nearbySegmentForStart.isConnectingInFrontOf(createdSegment)
                    && createdSegment.isConnectingInFrontOf(nearbySegmentForEnd) -> {
                nearbySegmentForStart.linkToBehindSegment(createdSegment)
                createdSegment.linkToBehindSegment(nearbySegmentForEnd)

                topSegments.remove(nearbySegmentForEnd)

                sequenceSegmentCreator.sequenceSegmentLinked(createdSegment)
            }
            nearbySegmentForStart != null && nearbySegmentForStart.isConnectingInFrontOf(createdSegment) -> {
                nearbySegmentForStart.linkToBehindSegment(createdSegment)
                sequenceSegmentCreator.sequenceSegmentLinked(createdSegment)
            }
            nearbySegmentForEnd != null && createdSegment.isConnectingInFrontOf(nearbySegmentForEnd) -> {
                createdSegment.linkToBehindSegment(nearbySegmentForEnd)
                topSegments.replace(newItem = createdSegment, oldItem = nearbySegmentForEnd)
                sequenceSegmentCreator.sequenceSegmentLinked(createdSegment)
            }
            else -> {
                val frontSegmentIndex = convertIndexToNearMaxIndexOfTopSegment(index)
                val insertPosition = frontSegmentIndex + 1
                topSegments.add(insertPosition, createdSegment)
            }
        }
    }

    override fun get(index: Int): T {
        checkIndex(index)

        // ToDo: improve performance for random access
        for (segment in segmentSequence()) {
            if (segment.firstIndex <= index && index <= segment.lastIndex) {
                return segment[index - segment.segmentOffset]
            }
        }

        throw IllegalStateException("value of index is not created. should call create function.")
    }

    override fun getOrCreate(index: Int, createSideRangeIfIndexNotCreated: Int): T {
        create(index, createSideRangeIfIndexNotCreated)
        return get(index)
    }

    private fun checkIndex(index: Int) {
        if (index < 0 || size <= index) {
            throw IndexOutOfBoundsException("outOfRange index: $index, size: $size")
        }
    }

    private fun segmentSequence(): kotlin.sequences.Sequence<ISequenceSegment<T>> {
        return sequence {
            for (firstSegment in topSegments) {
                var segment: ISequenceSegment<T>? = firstSegment
                while (segment != null) {
                    yield(checkNotNull(segment))
                    segment = segment.nextSegment
                }
            }
        }
    }

    private fun getNearbySegmentForStart(index: Int): ISequenceSegment<T>? {
        var maxIndexedSegment: ISequenceSegment<T>? = null
        for (segment in segmentSequence()) {
            if (segment.firstIndex <= index) {
                maxIndexedSegment = segment
            }
        }
        return maxIndexedSegment
    }

    private fun getNearbySegmentForEnd(index: Int): ISequenceSegment<T>? {
        for (segment in segmentSequence()) {
            if (index <= segment.lastIndex) {
                return segment
            }
        }
        return null
    }

    /**
     * if not found, return -1
     */
    private fun convertIndexToNearMaxIndexOfTopSegment(index: Int): Int {
        var maxIndex: Int? = null
        for ((i, segment) in topSegments.withIndex()) {
            if (segment.lastIndex <= index) {
                maxIndex = i
            }
        }
        return maxIndex ?: -1
    }

    private fun <T> ISequenceSegment<T>.isConnectingInFrontOf(behindSegment: ISequenceSegment<T>): Boolean {
        return lastIndex + 1 == behindSegment.firstIndex
    }

    private fun <T> ISequenceSegment<T>.linkToBehindSegment(behindSegment: ISequenceSegment<T>) {
        nextSegment = behindSegment
        behindSegment.previousSegment = this
    }

    private fun <T> IMutableList<T>.replace(newItem: T, oldItem: T) {
        val index = indexOf(oldItem)
        if (0 <= index) {
            this[index] = newItem
        }
    }
}