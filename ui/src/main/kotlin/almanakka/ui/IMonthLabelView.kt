package almanakka.ui

import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import android.graphics.Canvas

interface IMonthLabelView {

    fun getMeasuredWidth(): Int

    fun getMeasuredHeight(): Int

    fun draw(canvas: Canvas)

    fun measure(widthSpec: Int, heightSpec: Int)

    fun layout(left: Int, top: Int, right: Int, bottom: Int)

    fun setMonth(behaviorContainer: IBehaviorContainer, month: IMonth)
}