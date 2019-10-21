package almanakka.ui.behaviors

import almanakka.core.IDay
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehavior
import almanakka.core.behaviors.IBehaviorContainer
import androidx.appcompat.widget.AppCompatTextView

interface IDayTextBehavior : IBehavior {

    fun isProvide(behaviorContainer: IBehaviorContainer, month: IMonth, day: IDay): Boolean

    fun setTextStyleAndPrecomputedTextFuture(
            behaviorContainer: IBehaviorContainer,
            month: IMonth,
            day: IDay,
            textView: AppCompatTextView
    )
}