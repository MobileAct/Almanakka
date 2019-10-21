package almanakka.ui

import almanakka.core.IDay
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer

interface IDayView {

    fun getMeasuredWidth(): Int

    fun getMeasuredHeight(): Int

    fun measure(widthSpec: Int, heightSpec: Int)

    fun layout(left: Int, top: Int, right: Int, bottom: Int)

    fun setDay(behaviorContainer: IBehaviorContainer, month: IMonth, day: IDay?)

    fun getDay(): IDay?
}