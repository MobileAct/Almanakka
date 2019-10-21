package almanakka.ui

import almanakka.ui.configurations.Config
import android.annotation.SuppressLint
import android.content.Context
import androidx.core.widget.NestedScrollView

@SuppressLint("ViewConstructor")
internal class ScrollMonthContainerView(
        context: Context,
        viewProperty: ViewProperty,
        selectionProvider: ISelectionProvider,
        config: Config) : NestedScrollView(context), IMonthContainerView {

    override val monthView = MonthView(context, viewProperty, selectionProvider, config).also {
        addView(it)
    }
}