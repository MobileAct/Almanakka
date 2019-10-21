package almanakka.core.behaviors

import almanakka.core.IDay

interface ISelectableBehavior : IBehavior {

    fun isSelected(day: IDay): Boolean
}