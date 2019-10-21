package almanakka.ui.behaviors

import almanakka.core.DayOfWeek
import almanakka.core.IDay
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.IFuture
import almanakka.ui.configurations.DayConfig
import almanakka.ui.extensions.setTextAppearanceCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

class DayTextBehavior(private val dayConfig: DayConfig) : IDayTextBehavior {

    private class PrecomputedTextFuturePack(private val parameter: PrecomputedTextCompat.Params) {

        private val precomputedTextFutureCache = mutableMapOf<Byte, IFuture<PrecomputedTextCompat>>()

        fun get(day: Byte): IFuture<PrecomputedTextCompat> {
            return precomputedTextFutureCache.getOrPut(day) {
                PrecomputedTextCompat.getTextFuture(day.toString(), parameter, null)
            }
        }
    }

    private val precomputedTextFuturePacks = mutableMapOf<Int, PrecomputedTextFuturePack>()

    override fun isProvide(behaviorContainer: IBehaviorContainer, month: IMonth, day: IDay): Boolean {
        return true
    }

    override fun setTextStyleAndPrecomputedTextFuture(
            behaviorContainer: IBehaviorContainer,
            month: IMonth,
            day: IDay,
            textView: AppCompatTextView) {
        val textAppearanceResource = getTextAppearanceResource(behaviorContainer, month, day)
        textView.setTextAppearanceCompat(textAppearanceResource)

        val precomputedTextFuturePack = precomputedTextFuturePacks.getOrPut(textAppearanceResource) {
            PrecomputedTextFuturePack(TextViewCompat.getTextMetricsParams(textView))
        }

        textView.setTextFuture(precomputedTextFuturePack.get(day.day))
    }

    private fun getTextAppearanceResource(behaviorContainer: IBehaviorContainer, month: IMonth, day: IDay): Int {
        if (behaviorContainer.isSelected(day)) {
            return when (day.dayOfWeek) {
                DayOfWeek.Sunday -> dayConfig.sundaySelectedTextAppearance
                DayOfWeek.Saturday -> dayConfig.saturdaySelectedTextAppearance
                else -> dayConfig.weekdaySelectedTextAppearance
            }
        }

        if (behaviorContainer.isDisable(day)) {
            return when (day.dayOfWeek) {
                DayOfWeek.Sunday -> dayConfig.sundayDisabledTextAppearance
                DayOfWeek.Saturday -> dayConfig.saturdayDisabledTextAppearance
                else -> dayConfig.weekdayDisabledTextAppearance
            }
        }

        if (month.month != day.month) {
            return when (day.dayOfWeek) {
                DayOfWeek.Sunday -> dayConfig.sundayOfDifferentMonthTextAppearance
                DayOfWeek.Saturday -> dayConfig.saturdayOfDifferentMonthTextAppearance
                else -> dayConfig.weekdayOfDifferentMonthTextAppearance
            }
        }

        return when (day.dayOfWeek) {
            DayOfWeek.Sunday -> dayConfig.sundayTextAppearance
            DayOfWeek.Saturday -> dayConfig.saturdayTextAppearance
            else -> dayConfig.weekdayTextAppearance
        }
    }
}