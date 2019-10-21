package almanakka.ui.behaviors

import almanakka.core.IMonth
import almanakka.ui.configurations.MonthLabelConfig
import almanakka.ui.extensions.setTextAppearanceCompat
import androidx.appcompat.widget.AppCompatTextView

class MonthLabelTextBehavior(private val monthLabelConfig: MonthLabelConfig) : IMonthLabelTextBehavior {

    override fun isProvide(month: IMonth): Boolean {
        return true
    }

    override fun setTextStyleAndText(month: IMonth, textView: AppCompatTextView) {
        textView.setTextAppearanceCompat(monthLabelConfig.textAppearance)

        textView.text = getMonthLabel(month)
    }

    private fun getMonthLabel(month: IMonth): String {
        val baseLabel = when (month.month) {
            1.toByte() -> monthLabelConfig.januaryLabel
            2.toByte() -> monthLabelConfig.februaryLabel
            3.toByte() -> monthLabelConfig.marchLabel
            4.toByte() -> monthLabelConfig.aprilLabel
            5.toByte() -> monthLabelConfig.mayLabel
            6.toByte() -> monthLabelConfig.juneLabel
            7.toByte() -> monthLabelConfig.julyLabel
            8.toByte() -> monthLabelConfig.augustLabel
            9.toByte() -> monthLabelConfig.septemberLabel
            10.toByte() -> monthLabelConfig.octoberLabel
            11.toByte() -> monthLabelConfig.novemberLabel
            12.toByte() -> monthLabelConfig.decemberLabel
            else -> throw IllegalStateException("out of range, month")
        }

        return String.format(baseLabel, month.year, month.month)
    }
}