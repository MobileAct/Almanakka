package almanakka.ui

import almanakka.core.IWeek
import almanakka.core.behaviors.IBehaviorContainer
import android.content.Context
import android.view.View

abstract class BackgroundView(context: Context) : View(context) {

    abstract fun updateState(behaviorContainer: IBehaviorContainer, week: IWeek?)
}