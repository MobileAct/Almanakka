package almanakka.ui

import almanakka.core.ICalendarCollection
import almanakka.ui.configurations.Config
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MonthAdapter(
        calendar: ICalendarCollection,
        private val viewProperty: ViewProperty,
        private val selectionProvider: ISelectionProvider,
        private val config: Config) : RecyclerView.Adapter<MonthViewHolder>() {

    var calendar: ICalendarCollection = calendar
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return calendar[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return calendar.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(parent.context, viewProperty, selectionProvider, config)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.onBind(calendar.behaviorContainer, calendar[position], calendar.dayOfWeekOrders)
    }
}