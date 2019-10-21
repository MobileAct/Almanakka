package almanakka.ui.configurations

import almanakka.ui.IAttributeSet
import almanakka.ui.R
import almanakka.ui.extensions.convertToPixel
import android.content.Context
import androidx.annotation.Px
import androidx.annotation.StyleRes

class DayConfig(context: Context, attrs: IAttributeSet? = null, defStyleAttr: Int = -1) {

    companion object {

        private const val defaultDayPaddingDp = 8F
        private const val defaultDayMarginDp = 4F
    }

    @Px
    val margin: Int

    @Px
    val padding: Int

    @StyleRes
    val weekdayTextAppearance: Int

    @StyleRes
    val weekdayDisabledTextAppearance: Int

    @StyleRes
    val weekdaySelectedTextAppearance: Int

    @StyleRes
    val weekdayOfDifferentMonthTextAppearance: Int

    @StyleRes
    val saturdayTextAppearance: Int

    @StyleRes
    val saturdayDisabledTextAppearance: Int

    @StyleRes
    val saturdaySelectedTextAppearance: Int

    @StyleRes
    val saturdayOfDifferentMonthTextAppearance: Int

    @StyleRes
    val sundayTextAppearance: Int

    @StyleRes
    val sundayDisabledTextAppearance: Int

    @StyleRes
    val sundaySelectedTextAppearance: Int

    @StyleRes
    val sundayOfDifferentMonthTextAppearance: Int

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        padding = typedArray.getDimensionPixelSize(R.styleable.CalendarView_dayPadding, context.convertToPixel(defaultDayPaddingDp).toInt())
        margin = typedArray.getDimensionPixelSize(R.styleable.CalendarView_dayMargin, context.convertToPixel(defaultDayMarginDp).toInt())

        weekdayTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_weekdayTextAppearance,
                R.style.Almanakka_TextAppearance_Weekday
        )
        weekdayDisabledTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_weekdayDisabledTextAppearance,
                R.style.Almanakka_TextAppearance_Disabled
        )
        weekdaySelectedTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_weekdaySelectedTextAppearance,
                R.style.Almanakka_TextAppearance_Selected
        )
        weekdayOfDifferentMonthTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_weekdayOfDifferentMonthTextAppearance,
                R.style.Almanakka_TextAppearance_DifferentMonth
        )

        saturdayTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_saturdayTextAppearance,
                R.style.Almanakka_TextAppearance_Saturday
        )
        saturdayDisabledTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_saturdayDisabledTextAppearance,
                R.style.Almanakka_TextAppearance_Disabled
        )
        saturdaySelectedTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_saturdaySelectedTextAppearance,
                R.style.Almanakka_TextAppearance_Selected
        )
        saturdayOfDifferentMonthTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_saturdayOfDifferentMonthTextAppearance,
                R.style.Almanakka_TextAppearance_DifferentMonth
        )

        sundayTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_sundayTextAppearance,
                R.style.Almanakka_TextAppearance_Sunday
        )
        sundayDisabledTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_sundayDisabledTextAppearance,
                R.style.Almanakka_TextAppearance_Disabled
        )
        sundaySelectedTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_sundaySelectedTextAppearance,
                R.style.Almanakka_TextAppearance_Selected
        )
        sundayOfDifferentMonthTextAppearance = typedArray.getResourceId(
                R.styleable.CalendarView_sundayOfDifferentMonthTextAppearance,
                R.style.Almanakka_TextAppearance_DifferentMonth
        )

        typedArray.recycle()
    }
}