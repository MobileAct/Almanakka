package almanakka.core.behaviors

import almanakka.core.IList

class BehaviorContainer : IBehaviorContainer {

    private val mutableBehaviors = mutableListOf<IBehavior>()
    override val behaviors: IList<IBehavior>
        get() = mutableBehaviors

    private val mutableDisableBehaviors = mutableListOf<IDisableBehavior>()
    override val disableBehaviors: IList<IDisableBehavior>
        get() = mutableDisableBehaviors

    private val mutableSelectableBehaviors = mutableListOf<ISelectableBehavior>()
    override val selectableBehaviors: IList<ISelectableBehavior>
        get() = mutableSelectableBehaviors


    override fun register(behavior: IBehavior) {
        when (behavior) {
            is IDisableBehavior -> mutableDisableBehaviors.add(behavior)
            is ISelectableBehavior -> mutableSelectableBehaviors.add(behavior)
        }
        mutableBehaviors.add(behavior)
    }
}