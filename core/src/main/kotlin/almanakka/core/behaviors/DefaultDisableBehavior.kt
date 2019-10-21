package almanakka.core.behaviors

import almanakka.core.IDay

internal class DefaultDisableBehavior(private val minDay: IDay, private val maxDay: IDay) : IDisableBehavior {

    override fun isDisable(day: IDay): Boolean {
        return day < minDay || maxDay < day
    }
}