package almanakka.ui.configurations

import almanakka.ui.IAttributeSet
import almanakka.ui.R
import almanakka.ui.extensions.convertToPixel
import android.content.Context
import androidx.annotation.Px
import androidx.annotation.StyleRes

class MonthLabelConfig(context: Context, attrs: IAttributeSet? = null, defStyleAttr: Int = -1) {

    companion object {

        private const val defaultMonthLabelPaddingDp = 8F
        private const val defaultMonthLabelMarginDp = 4F
    }

    @Px
    val margin: Int

    @Px
    val padding: Int

    @StyleRes
    val textAppearance: Int

    val januaryLabel: String
    val februaryLabel: String
    val marchLabel: String
    val aprilLabel: String
    val mayLabel: String
    val juneLabel: String
    val julyLabel: String
    val augustLabel: String
    val septemberLabel: String
    val octoberLabel: String
    val novemberLabel: String
    val decemberLabel: String

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        padding = typedArray.getDimensionPixelSize(
                R.styleable.CalendarView_monthLabelPadding,
                context.convertToPixel(defaultMonthLabelPaddingDp).toInt()
        )
        margin = typedArray.getDimensionPixelOffset(
                R.styleable.CalendarView_monthLabelMargin,
                context.convertToPixel(defaultMonthLabelMarginDp).toInt()
        )

        textAppearance = typedArray.getResourceId(R.styleable.CalendarView_monthLabelTextAppearance, R.style.Almanakka_TextAppearance_MonthLabel)

        val monthLabelsId = typedArray.getResourceId(R.styleable.CalendarView_monthLabels, R.array.months)
        val monthLabels = context.resources.getStringArray(monthLabelsId)

        check(monthLabels.size == 12) { "month label must have 12 elements" }

        januaryLabel = monthLabels[0]
        februaryLabel = monthLabels[1]
        marchLabel = monthLabels[2]
        aprilLabel = monthLabels[3]
        mayLabel = monthLabels[4]
        juneLabel = monthLabels[5]
        julyLabel = monthLabels[6]
        augustLabel = monthLabels[7]
        septemberLabel = monthLabels[8]
        octoberLabel = monthLabels[9]
        novemberLabel = monthLabels[10]
        decemberLabel = monthLabels[11]

        typedArray.recycle()
    }
}