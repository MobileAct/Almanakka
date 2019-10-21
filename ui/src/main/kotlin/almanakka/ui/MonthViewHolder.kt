package almanakka.ui

import almanakka.core.DayOfWeek
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.Config
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MonthViewHolder(
        context: Context,
        viewProperty: ViewProperty,
        selectionProvider: ISelectionProvider,
        config: Config) : RecyclerView.ViewHolder(createMonthView(context, viewProperty, selectionProvider, config)) {

    companion object {

        private fun createMonthView(
                context: Context,
                viewProperty: ViewProperty,
                selectionProvider: ISelectionProvider,
                config: Config): View {
            if (viewProperty.orientation == ViewProperty.Orientation.Horizontal) {
                return ScrollMonthContainerView(context, viewProperty, selectionProvider, config)
            }

            return MonthView(context, viewProperty, selectionProvider, config)
        }
    }

    private val monthView = itemView.let {
        val monthContainerView = it as IMonthContainerView
        monthContainerView.monthView
    }

    fun onBind(behaviorContainer: IBehaviorContainer, month: IMonth, dayOfWeekOrders: Array<DayOfWeek>) {
        monthView.setMonth(behaviorContainer, month, dayOfWeekOrders)
    }
}