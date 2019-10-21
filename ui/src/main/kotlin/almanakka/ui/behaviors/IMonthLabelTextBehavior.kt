package almanakka.ui.behaviors

import almanakka.core.IMonth
import almanakka.core.behaviors.IBehavior
import androidx.appcompat.widget.AppCompatTextView

interface IMonthLabelTextBehavior : IBehavior {

    fun isProvide(month: IMonth): Boolean

    fun setTextStyleAndText(
            month: IMonth,
            textView: AppCompatTextView
    )
}