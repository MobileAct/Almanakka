package almanakka.core.animators

import almanakka.core.CalendarCollection
import almanakka.core.Day
import almanakka.core.DayOfWeek
import org.junit.jupiter.api.Test

class AnimatorTest {

    class TestClock(var time: Long) : IClock {

        override fun currentTimeMilliSeconds(): Long {
            return time
        }
    }

    private val calendar = CalendarCollection.create(
            Day(2019, 1, 1, DayOfWeek.firstday),
            Day(2019, 1, 10, DayOfWeek.firstday),
            DayOfWeek.firstday,
            false
    )
    val days = calendar[0].asSequence()
            .flatMap { it.days.asSequence() }
            .filterNotNull()
            .filter { calendar.behaviorContainer.isDisable(it).not() }
            .toList()

    // return selected 1/5 animator
    private fun create(clock: IClock): IAnimator {
        return Animator(clock, 100, calendar[0].firstWeek.lastDay)
    }

    private fun test(animator: IAnimator, progress: IntArray, isAnimating: Boolean) {
        assert(days.size == progress.size) { "days:${days.size}, progress:${progress.size}" }

        fun Progress.toPercent(): Int {
            return when (this) {
                is Progress.None -> 0
                is Progress.Complete -> 100
                is Progress.LeftToRight -> this.percent
                is Progress.RightToLeft -> this.percent
            }
        }

        for ((percent, day) in progress.zip(days)) {
            assert(animator.state(day).toPercent() == percent)
        }
        assert(animator.isAnimating() == isAnimating)
    }

    @Test
    fun test1() {
        // expand: 1/5 => 1/5 ~ 1/9
        val clock = TestClock(0)
        val animator = create(clock)

        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)

        animator.select(days[4], days[8])

        clock.time += 50
        test(animator, intArrayOf(0, 0, 0, 0, 100, 50, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 50, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 50, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 100, 50, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 100, 75, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 100, 100, 0), false)
    }

    @Test
    fun test2() {
        // expand: 1/5 => 1/2 ~ 1/5
        val clock = TestClock(0)
        val animator = create(clock)

        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)

        animator.select(days[1], days[4])

        clock.time += 50
        test(animator, intArrayOf(0, 0, 0, 50, 100, 0, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 50, 100, 100, 0, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 50, 100, 100, 100, 0, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 75, 100, 100, 100, 0, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 0, 0, 0, 0, 0), false)
    }

    @Test
    fun test3() {
        // expand: 1/5 => 1/2 ~ 1/9
        val clock = TestClock(0)
        val animator = create(clock)

        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)

        animator.select(days[1], days[8])
        clock.time += 50
        test(animator, intArrayOf(0, 0, 0, 50, 100, 50, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 50, 100, 100, 100, 50, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 50, 100, 100, 100, 100, 100, 50, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 75, 100, 100, 100, 100, 100, 75, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 25, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 50, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 75, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 100, 0), false)
    }

    @Test
    fun test4() {
        // shrink: 1/5 ~ 1/9 => 1/5
        val clock = TestClock(0)
        val animator = create(clock)
        animator.select(days[4], days[8])
        clock.time += 1000

        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 100, 100, 0), false)

        animator.select(days[4])
        clock.time += 50
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 100, 50, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 100, 50, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 50, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 0, 100, 50, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 25, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)
    }

    @Test
    fun test5() {
        // shrink: 1/2 ~ 1/5 => 1/5
        val clock = TestClock(0)
        val animator = create(clock)
        animator.select(days[1], days[4])
        clock.time += 1000

        test(animator, intArrayOf(0, 100, 100, 100, 100, 0, 0, 0, 0, 0), false)

        animator.select(days[4])

        clock.time += 50
        test(animator, intArrayOf(0, 50, 100, 100, 100, 0, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 50, 100, 100, 0, 0, 0, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 50, 100, 0, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 25, 100, 0, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)
    }

    @Test
    fun test6() {
        // shrink: 1/2 ~ 1/9 => 1/5
        val clock = TestClock(0)
        val animator = create(clock)
        animator.select(days[1], days[8])
        clock.time += 1000

        test(animator, intArrayOf(0, 100, 100, 100, 100, 100, 100, 100, 100, 0), false)

        animator.select(days[4])
        clock.time += 50
        test(animator, intArrayOf(0, 50, 100, 100, 100, 100, 100, 100, 50, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 50, 100, 100, 100, 100, 50, 0, 0), true)
        clock.time += 100
        test(animator, intArrayOf(0, 0, 0, 50, 100, 100, 50, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 25, 100, 100, 25, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 100, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 75, 0, 0, 0, 0), true)
        clock.time += 50
        test(animator, intArrayOf(0, 0, 0, 0, 100, 25, 0, 0, 0, 0), true)
        clock.time += 25
        test(animator, intArrayOf(0, 0, 0, 0, 100, 0, 0, 0, 0, 0), false)
    }
}