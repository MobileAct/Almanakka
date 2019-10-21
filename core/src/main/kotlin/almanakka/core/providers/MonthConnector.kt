package almanakka.core.providers

import almanakka.core.Day
import almanakka.core.Month

class MonthConnector : IMonthConnector {

    override fun connect(front: Month, behind: Month) {
        val frontWeek = front.lastWeek
        val behindWeek = behind.firstWeek

        check(frontWeek.days.size == behindWeek.days.size)

        if (frontWeek.days.count { it != null } == 7) {
            // behindWeek not check for performance
            return
        }

        for (i in 0 until frontWeek.days.size) {
            val dayOfFrontWeek = frontWeek.days[i]
            val dayOfBehindWeek = behindWeek.days[i]

            val day = dayOfFrontWeek xor dayOfBehindWeek

            checkNotNull(day)

            frontWeek.days[i] = day
            behindWeek.days[i] = day
        }
    }

    private infix fun Day?.xor(other: Day?): Day? {
        return when (this) {
            null -> when (other) {
                null -> null
                else -> other
            }
            else -> when (other) {
                null -> this
                else -> null
            }
        }
    }
}