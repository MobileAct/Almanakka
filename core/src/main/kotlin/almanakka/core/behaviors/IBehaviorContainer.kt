package almanakka.core.behaviors

import almanakka.core.IDay
import almanakka.core.IList

interface IBehaviorContainer {

    val behaviors: IList<IBehavior>
    val disableBehaviors: IList<IDisableBehavior>
    val selectableBehaviors: IList<ISelectableBehavior>

    fun register(behavior: IBehavior)

    fun isDisable(day: IDay): Boolean {
        for (behavior in disableBehaviors) {
            if (behavior.isDisable(day)) {
                return true
            }
        }
        return false
    }

    fun isSelected(day: IDay): Boolean {
        for (behavior in selectableBehaviors) {
            if (behavior.isSelected(day)) {
                return true
            }
        }
        return false
    }
}