package almanakka.ui

import almanakka.core.IDay
import almanakka.core.behaviors.IBehaviorContainer
import android.os.Bundle
import android.view.View.OnClickListener as IViewClickListener
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener as IRecyclerViewTouchListener

interface ISelectionProvider : IViewClickListener, IRecyclerViewTouchListener {

    val viewState: ViewState

    fun registerBehavior(behaviorContainer: IBehaviorContainer)

    fun select(day: IDay)

    fun createState(): Bundle

    fun restoreState(state: Bundle)
}