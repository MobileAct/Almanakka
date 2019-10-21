package almanakka.ui.extensions

import almanakka.core.DayOfWeek
import almanakka.core.IDay
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.behaviors.IDayLabelTextBehavior
import almanakka.ui.behaviors.IDayTextBehavior
import almanakka.ui.behaviors.IMonthLabelTextBehavior
import androidx.appcompat.widget.AppCompatTextView

fun IBehaviorContainer.setDayTextStyleAndPrecomputedTextFuture(
        month: IMonth,
        day: IDay,
        textView: AppCompatTextView
) {
    val dayTextBehavior = behaviors.asSequence()
            .filterIsInstance<IDayTextBehavior>()
            .filter { it.isProvide(this, month, day) }
            .lastOrNull() ?: return

    dayTextBehavior.setTextStyleAndPrecomputedTextFuture(this, month, day, textView)
}

fun IBehaviorContainer.setDayLabelTextStyleAndPrecomputedTextFuture(dayOfWeek: DayOfWeek, textView: AppCompatTextView) {
    val dayLabelTextBehavior = behaviors.asSequence()
            .filterIsInstance<IDayLabelTextBehavior>()
            .filter { it.isProvide(dayOfWeek) }
            .lastOrNull() ?: return

    dayLabelTextBehavior.setTextStyleAndPrecomputedTextFuture(dayOfWeek, textView)
}

fun IBehaviorContainer.setMonthLabelTextStyleAndText(month: IMonth, textView: AppCompatTextView) {
    val monthLabelTextBehavior = behaviors.asSequence()
            .filterIsInstance<IMonthLabelTextBehavior>()
            .filter { it.isProvide(month) }
            .lastOrNull() ?: return

    monthLabelTextBehavior.setTextStyleAndText(month, textView)
}