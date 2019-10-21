package almanakka.ui.behaviors

import almanakka.core.DayOfWeek
import almanakka.ui.IFuture
import almanakka.ui.configurations.DayLabelConfig
import almanakka.ui.extensions.setTextAppearanceCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

class DayLabelTextBehavior(private val dayLabelConfig: DayLabelConfig) : IDayLabelTextBehavior {

    private class PrecomputedTextFuturePack(private val parameter: PrecomputedTextCompat.Params) {

        private val precomputedTextFutureCache = mutableMapOf<String, IFuture<PrecomputedTextCompat>>()

        fun get(dayLabel: String): IFuture<PrecomputedTextCompat> {
            return precomputedTextFutureCache.getOrPut(dayLabel) {
                PrecomputedTextCompat.getTextFuture(dayLabel, parameter, null)
            }
        }
    }

    private val precomputedTextFuturePacks = mutableMapOf<Int, PrecomputedTextFuturePack>()

    override fun isProvide(dayOfWeek: DayOfWeek): Boolean {
        return true
    }

    override fun setTextStyleAndPrecomputedTextFuture(
            dayOfWeek: DayOfWeek,
            textView: AppCompatTextView) {
        val textAppearanceResource = getTextAppearanceResource(dayOfWeek)
        textView.setTextAppearanceCompat(textAppearanceResource)

        val precomputedTextFuturePack = precomputedTextFuturePacks.getOrPut(textAppearanceResource) {
            PrecomputedTextFuturePack(TextViewCompat.getTextMetricsParams(textView))
        }

        textView.setTextFuture(precomputedTextFuturePack.get(getDayLabel(dayOfWeek)))
    }

    private fun getTextAppearanceResource(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.Sunday -> dayLabelConfig.sundayLabelTextAppearance
            DayOfWeek.Saturday -> dayLabelConfig.saturdayLabelTextAppearance
            else -> dayLabelConfig.weekdayLabelTextAppearance
        }
    }

    private fun getDayLabel(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.Sunday -> dayLabelConfig.sundayLabel
            DayOfWeek.Monday -> dayLabelConfig.mondayLabel
            DayOfWeek.Tuesday -> dayLabelConfig.tuesdayLabel
            DayOfWeek.Wednesday -> dayLabelConfig.wednesdayLabel
            DayOfWeek.Thursday -> dayLabelConfig.thursdayLabel
            DayOfWeek.Friday -> dayLabelConfig.fridayLabel
            DayOfWeek.Saturday -> dayLabelConfig.saturdayLabel
        }
    }
}