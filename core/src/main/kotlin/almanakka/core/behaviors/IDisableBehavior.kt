package almanakka.core.behaviors

import almanakka.core.IDay

interface IDisableBehavior : IBehavior {

    fun isDisable(day: IDay): Boolean
}