package almanakka.core

import almanakka.core.behaviors.BehaviorContainer
import almanakka.core.behaviors.DefaultDisableBehavior
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.core.collections.ISequence
import almanakka.core.collections.ISequenceSegment
import almanakka.core.collections.ISequenceSegmentCreator
import almanakka.core.collections.SequenceSegment
import almanakka.core.providers.*

// ToDo: Behaviors
class CalendarCollection private constructor(
        override val minDay: IDay,
        override val maxDay: IDay,
        private val monthCreator: IMonthCreator,
        private val monthConnector: IMonthConnector,
        private val dayConnector: IDayConnector,
        override val behaviorContainer: IBehaviorContainer) : ICalendarCollection {

    companion object {

        fun create(
                minDay: IDay,
                maxDay: IDay,
                dayOfWeekOrderStart: DayOfWeek,
                isShowDaysOfDifferentMonth: Boolean): CalendarCollection {
            val dayConnector = DayConnector()
            val monthConnector = MonthConnectorFactory.create(isShowDaysOfDifferentMonth)
            val monthCreator = MonthCreator(dayConnector).apply {
                this.dayOfWeekOrderStart = dayOfWeekOrderStart
            }
            val behaviorContainer = BehaviorContainer().apply {
                register(DefaultDisableBehavior(minDay, maxDay))
            }

            return create(minDay, maxDay, monthCreator, monthConnector, dayConnector, behaviorContainer)
        }

        fun create(
                minDay: IDay,
                maxDay: IDay,
                monthCreator: IMonthCreator,
                monthConnector: IMonthConnector,
                dayConnector: IDayConnector,
                behaviorContainer: IBehaviorContainer): CalendarCollection {
            return CalendarCollection(minDay, maxDay, monthCreator, monthConnector, dayConnector, behaviorContainer)
        }
    }

    private inner class SequenceSegmentCreator : ISequenceSegmentCreator<Month> {

        override fun createSequenceSegment(startIndex: Int, endIndex: Int): ISequenceSegment<Month> {
            val size = endIndex - startIndex + 1

            val months = Array(size) {
                val (year, month) = checkNotNull(monthIndexer.convertIndexToMonth(startIndex + it))
                monthCreator.create(year, month)
            }

            linkMonths(months)

            return SequenceSegment.create(months)
        }

        private fun linkMonths(months: Array<Month>) {
            for (i in 0 until (months.size - 1)) {
                val frontMonth = months[i]
                val behindMonth = months[i + 1]

                dayConnector.connect(frontMonth.lastWeek.lastDay, behindMonth.firstWeek.firstDay)
                monthConnector.connect(frontMonth, behindMonth)
            }
        }

        override fun sequenceSegmentLinked(linkedSegment: ISequenceSegment<Month>) {
            val previousSegment = linkedSegment.previousSegment
            val nextSegment = linkedSegment.nextSegment

            if (previousSegment != null) {
                dayConnector.connect(previousSegment.last.lastWeek.lastDay, linkedSegment.first.firstWeek.firstDay)
                monthConnector.connect(previousSegment.last, linkedSegment.first)
            }

            if (nextSegment != null) {
                dayConnector.connect(linkedSegment.last.lastWeek.lastDay, nextSegment.first.firstWeek.firstDay)
                monthConnector.connect(linkedSegment.last, nextSegment.first)
            }
        }
    }

    private val sequence: ISequence<Month>
    private val monthIndexer = MonthIndexer(minDay, maxDay)
    override val size = monthIndexer.size
    override val dayOfWeekOrders = monthCreator.createDayOfWeekOrders()

    init {
        sequence = almanakka.core.collections.Sequence(SequenceSegmentCreator(), size)
    }

    override fun get(index: Int): Month {
        // ToDo: optimize createSideRangeIfIndexNotCreate
        val result = sequence.getOrCreate(index, 12)

        // create edge value
        if (0 < index) {
            sequence.create(index - 1, 1)
        }
        if (index < size - 1) {
            sequence.create(index + 1, 1)
        }

        return result
    }

    override fun convertMonthToIndex(year: Short, month: Byte): Int? {
        return monthIndexer.convertMonthToIndex(year, month)
    }
}