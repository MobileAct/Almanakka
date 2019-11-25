package almanakka.ui.internal

import almanakka.ui.WeekView
import almanakka.ui.configurations.ViewConfig

object ViewMeasure {

    fun measureDayWidth(viewConfig: ViewConfig, width: Int): IntArray {
        val contentWidth = width - viewConfig.monthPaddingSide * 2
        val widthPerElement = contentWidth / WeekView.daySize
        val remOfWidthPerElement = contentWidth % WeekView.daySize

        return IntArray(WeekView.daySize) {
            widthPerElement + if (it < remOfWidthPerElement) 1 else 0
        }
    }
}