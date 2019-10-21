package almanakka.ui.configurations

import almanakka.ui.IAttributeSet
import almanakka.ui.R
import almanakka.ui.extensions.convertToPixel
import android.content.Context
import androidx.annotation.Px
import androidx.annotation.StyleRes

class DayLabelConfig(context: Context, attrs: IAttributeSet? = null, defStyleAttr: Int = -1) {

    companion object {

        private const val defaultDayLabelPaddingDp = 8F
        private const val defaultDayLabelMarginDp = 4F
    }

    @Px
    val padding: Int

    @Px
    val margin: Int

    @StyleRes
    val weekdayLabelTextAppearance: Int

    @StyleRes
    val saturdayLabelTextAppearance: Int

    @StyleRes
    val sundayLabelTextAppearance: Int

    val sundayLabel: String
    val mondayLabel: String
    val tuesdayLabel: String
    val wednesdayLabel: String
    val thursdayLabel: String
    val fridayLabel: String
    val saturdayLabel: String

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        padding = typedArray.getDimensionPixelSize(
                R.styleable.CalendarView_dayLabelPadding,
                context.convertToPixel(defaultDayLabelPaddingDp).toInt()
        )
        margin = typedArray.getDimensionPixelSize(
                R.styleable.CalendarView_dayLabelMargin,
                context.convertToPixel(defaultDayLabelMarginDp).toInt()
        )

        weekdayLabelTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_weekdayLabelTextAppearance,
                R.style.Almanakka_TextAppearance_Weekday
        )
        saturdayLabelTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_saturdayLabelTextAppearance,
                R.style.Almanakka_TextAppearance_Saturday
        )
        sundayLabelTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_sundayLabelTextAppearance,
                R.style.Almanakka_TextAppearance_Sunday
        )

        val dayOfWeekLabelsId = typedArray.getResourceId(R.styleable.CalendarView_dayOfWeekLabels, R.array.dayOfWeeks)
        val dayOfWeekLabels = context.resources.getStringArray(dayOfWeekLabelsId)

        check(dayOfWeekLabels.size == 7) { "day label must have 7 elements" }

        sundayLabel = dayOfWeekLabels[0]
        mondayLabel = dayOfWeekLabels[1]
        tuesdayLabel = dayOfWeekLabels[2]
        wednesdayLabel = dayOfWeekLabels[3]
        thursdayLabel = dayOfWeekLabels[4]
        fridayLabel = dayOfWeekLabels[5]
        saturdayLabel = dayOfWeekLabels[6]

        typedArray.recycle()
    }
}