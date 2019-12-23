package almanakka.core.animators

import almanakka.core.IDay

class Animator(
        private val clock: IClock,
        private val durationPerDayMilliSeconds: Int,
        selectedDay: IDay
) : IAnimator {

    class DayState(val day: IDay) {

        constructor(day: IDay, progress: Progress) : this(day) {
            this.progress = progress
        }

        var progress: Progress = Progress.None
    }

    private var measurementTimeMilliSeconds = clock.currentTimeMilliSeconds()
    private var selectedLeft: IDay = selectedDay
    private var selectedRight: IDay = selectedDay
    private var dayStateLeft: DayState = DayState(selectedDay, Progress.RightToLeft(100))
    private var dayStateRight: DayState = DayState(selectedDay, Progress.LeftToRight(100))

    constructor(durationPerDayMilliSeconds: Int, selectedDay: IDay)
            : this(Clock, durationPerDayMilliSeconds, selectedDay)

    override fun updateMeasureTime() {
        measurementTimeMilliSeconds = clock.currentTimeMilliSeconds()
    }

    override fun select(day: IDay) {
        this.selectedLeft = day
        this.selectedRight = day
    }

    override fun selectWithNoAnimation(day: IDay) {
        select(day)

        dayStateLeft = DayState(day, Progress.RightToLeft(100))
        dayStateRight = DayState(day, Progress.LeftToRight(100))
    }

    override fun select(startDay: IDay, endDay: IDay) {
        if (endDay < startDay) {
            throw Exception("startDay must more than endDay")
        }
        this.selectedLeft = startDay
        this.selectedRight = endDay
    }

    override fun selectWithNoAnimation(startDay: IDay, endDay: IDay) {
        select(startDay, endDay)

        dayStateLeft = DayState(startDay, Progress.RightToLeft(100))
        dayStateRight = DayState(startDay, Progress.LeftToRight(100))
    }

    override fun state(day: IDay): Progress {
        measure()

        if (day < dayStateLeft.day || dayStateRight.day < day) {
            return Progress.None
        }
        if (dayStateLeft.day < day && day < dayStateRight.day) {
            return Progress.Complete
        }

        if (dayStateLeft.day == dayStateRight.day) {
            return Progress.Complete
        }
        if (dayStateLeft.day == day) {
            return dayStateLeft.progress
        }
        if (dayStateRight.day == day) {
            return dayStateRight.progress
        }

        return Progress.None // unknown case
    }

    private fun measure() {
        if (measurementTimeMilliSeconds == clock.currentTimeMilliSeconds()) {
            return
        }

        measureLeft()
        measureRight()

        measurementTimeMilliSeconds = clock.currentTimeMilliSeconds()
    }

    private fun measureLeft() {
        val time = clock.currentTimeMilliSeconds() - measurementTimeMilliSeconds
        var maybeWalkedDaysPercent = time.toFloat() / durationPerDayMilliSeconds * 100
        val statePercent = when (val progress = dayStateLeft.progress) {
            is Progress.Complete -> 100
            is Progress.None -> 0
            is Progress.LeftToRight -> throw IllegalStateException("illegal left state")
            is Progress.RightToLeft -> progress.percent
        }

        fun newProgress(percent: Int): Progress {
            return when {
                percent <= 0 -> Progress.RightToLeft(0)
                percent in 1..99 -> Progress.RightToLeft(percent)
                else -> Progress.RightToLeft(100)
            }
        }

        when {
            selectedLeft == dayStateLeft.day -> {
                val percent = statePercent + maybeWalkedDaysPercent
                dayStateLeft.progress = newProgress(percent.toInt())
            }
            selectedLeft < dayStateLeft.day -> {
                // move to expand
                maybeWalkedDaysPercent += statePercent

                val maybeWalkedDayCount: Int = (maybeWalkedDaysPercent / 100 + 1).toInt()
                val days = dayStateLeft.day
                        .previousDaySequence()
                        .take(maybeWalkedDayCount)
                        .takeWhile { selectedLeft <= it }
                        .toList()
                val day = days.last()
                maybeWalkedDaysPercent -= (days.size - 1) * 100

                val newProgress = newProgress(maybeWalkedDaysPercent.toInt())
                dayStateLeft = DayState(day, newProgress)
            }
            dayStateLeft.day < selectedLeft -> {
                // move to shrink
                maybeWalkedDaysPercent += 100 - statePercent
                val maybeWalkedDayCount: Int = (maybeWalkedDaysPercent / 100 + 1).toInt()
                val days = dayStateLeft.day
                        .nextDaySequence()
                        .take(maybeWalkedDayCount)
                        .takeWhile { it <= selectedLeft }
                        .toList()
                val day = days.last()
                maybeWalkedDaysPercent -= (days.size - 1) * 100

                val newProgress = newProgress(100 - maybeWalkedDaysPercent.toInt())
                dayStateLeft = DayState(day, newProgress)
            }
        }
    }

    private fun measureRight() {
        val time = clock.currentTimeMilliSeconds() - measurementTimeMilliSeconds
        var maybeWalkedDaysPercent = time.toFloat() / durationPerDayMilliSeconds * 100
        val statePercent = when (val progress = dayStateRight.progress) {
            is Progress.Complete -> 100
            is Progress.None -> 0
            is Progress.RightToLeft -> throw IllegalStateException("illegal right state")
            is Progress.LeftToRight -> progress.percent
        }

        fun newProgress(percent: Int): Progress {
            return when {
                percent <= 0 -> Progress.LeftToRight(0)
                percent in 1..99 -> Progress.LeftToRight(percent)
                else -> Progress.LeftToRight(100)
            }
        }

        when {
            selectedRight == dayStateRight.day -> {
                val percent = statePercent + maybeWalkedDaysPercent
                dayStateRight.progress = newProgress(percent.toInt())
            }
            dayStateRight.day < selectedRight -> {
                // move to expand
                maybeWalkedDaysPercent += statePercent
                val maybeWalkedDayCount: Int = (maybeWalkedDaysPercent / 100 + 1).toInt()
                val days = dayStateRight.day
                        .nextDaySequence()
                        .take(maybeWalkedDayCount)
                        .takeWhile { it <= selectedRight }
                        .toList()
                val day = days.last()
                maybeWalkedDaysPercent -= (days.size - 1) * 100
                val newProgress = newProgress(maybeWalkedDaysPercent.toInt())
                dayStateRight = DayState(day, newProgress)
            }
            selectedRight < dayStateRight.day -> {
                // move to shrink
                maybeWalkedDaysPercent += 100 - statePercent
                val maybeWalkedDayCount: Int = (maybeWalkedDaysPercent / 100 + 1).toInt()
                val days = dayStateRight.day
                        .previousDaySequence()
                        .take(maybeWalkedDayCount)
                        .takeWhile { selectedRight <= it }
                        .toList()
                val day = days.last()
                maybeWalkedDaysPercent -= (days.size - 1) * 100

                val newProgress = newProgress(100 - maybeWalkedDaysPercent.toInt())
                dayStateRight = DayState(day, newProgress)
            }
        }
    }

    override fun isAnimating(): Boolean {
        measure()

        if (dayStateLeft.day != selectedLeft || dayStateRight.day != selectedRight) {
            return true
        }

        val leftProgress = when (val left = dayStateLeft.progress) {
            is Progress.Complete -> 100
            is Progress.RightToLeft -> left.percent
            else -> 0
        }
        val rightProgress = when (val right = dayStateRight.progress) {
            is Progress.Complete -> 100
            is Progress.LeftToRight -> right.percent
            else -> 0
        }
        if (dayStateLeft.day == selectedLeft && dayStateRight.day == selectedRight
                && (leftProgress != 100 || rightProgress != 100)) {
            return true
        }

        return false
    }

    // include today
    private fun IDay.nextDaySequence(): Sequence<IDay> {
        return sequence {
            yield(this@nextDaySequence)
            var current = this@nextDaySequence.nextDay
            while (current != null) {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                yield(current!!) // force unwrap
                current = current.nextDay
            }
        }
    }

    // include today
    private fun IDay.previousDaySequence(): Sequence<IDay> {
        return sequence {
            yield(this@previousDaySequence)
            var current = this@previousDaySequence.previousDay
            while (current != null) {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                yield(current!!) // force unwrap
                current = current.previousDay
            }
        }
    }
}