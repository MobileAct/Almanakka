package almanakka.core.animators

import almanakka.core.IDay

interface IAnimator {

    fun select(day: IDay)

    fun selectWithNoAnimation(day: IDay)

    fun select(startDay: IDay, endDay: IDay)

    fun selectWithNoAnimation(startDay: IDay, endDay: IDay)

    fun state(day: IDay): Progress

    fun isAnimating(): Boolean
}