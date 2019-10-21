package almanakka.ui.behaviors

import almanakka.core.DayOfWeek
import almanakka.core.behaviors.IBehavior
import androidx.appcompat.widget.AppCompatTextView

interface IDayLabelTextBehavior : IBehavior {

    fun isProvide(dayOfWeek: DayOfWeek): Boolean

    fun setTextStyleAndPrecomputedTextFuture(
            dayOfWeek: DayOfWeek,
            textView: AppCompatTextView
    )
}